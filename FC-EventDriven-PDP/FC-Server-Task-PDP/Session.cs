using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace FC_EventDriven_PDP
{
    enum HEADER_READ_TYPE
    {
        READ_KEY,
        READ_VALUE,
    }

    internal class ResponseType
    {
        public string Message { get; private set; }

        public int Code { get; private set; }

        public string ResponseMessage { get; private set; }

        private ResponseType(int code, string message) {
            Code = code;
            Message = message;
            ResponseMessage = code.ToString() + " " + message;
        }

        public static ResponseType OK { get { return new ResponseType(200, "OK"); } }
        public static ResponseType BAD_REQUEST { get { return new ResponseType(403, "FORBIDDEN"); } }
    }

    internal class Session
    {
        private readonly string HTTP_VERSION = "HTTP/1.1";
        private readonly string END_HEADER_TOKEN = "\r\n\r\n";
        private readonly Socket _conn;
        private byte[] _buffer;
        private int _pos;
        private int _size;
        private char? _lastReadCharacter = null;
        private int _headerEndCount;
        private string _currentKey = "";
        private string _currentValue = "";
        private HEADER_READ_TYPE _currentHeaderReadType;
        private long _bodyReadLength = 0;
        private bool _isHttpVersionRead = false;
        private bool _isFirstBodyRead = true;
        Dictionary<string, string> _headers = new();

        private Session(Socket conn)
        {
            _conn = conn;
            _buffer = new byte[10];
        }

        private void Start()
        {
            _headers = new();
            _bodyReadLength = 0;
            _headerEndCount = 0;
            _pos = 0;
            _isHttpVersionRead = false;
            _isFirstBodyRead = true;
            _currentHeaderReadType = HEADER_READ_TYPE.READ_KEY;
            Task<int> future = Receive(_conn, _buffer, 0, _buffer.Length);
            future.ContinueWith(OnBytesReceived);
        }

        private void OnBytesReceived(Task<int> future)
        {
            _size = future.Result;
            ProcessBuffer();
        }

        private void SendHttpResponse(ResponseType responseType)
        {
            string responseString = "";
            responseString += HTTP_VERSION + " " + responseType.ResponseMessage;
            responseString += END_HEADER_TOKEN;

            byte[] b = new byte[responseString.Length + 1];
            for (int i = 0; i < responseString.Length; ++i)
            {
                b[i] = (byte)responseString[i];
            }
            Task<int> future = Send(_conn, b, 0, b.Length);
            future.ContinueWith(OnSendDone);
        }

        private void ProcessBuffer()
        {
            if (_size == 0)
            {
                SendHttpResponse(ResponseType.OK);
            }
            _pos = 0;
            while (_pos < _size)
            {
                byte b = _buffer[_pos];
                char c = (char)b;
                ++_pos;
                if (c != '\r' && c != '\n')
                {
                    _headerEndCount = 0;
                }
                
                if (c == '\r' || c == '\n')
                {
                    _isHttpVersionRead = true;
                }

                if (c == ':' && _isHttpVersionRead)
                {
                    _currentHeaderReadType = HEADER_READ_TYPE.READ_VALUE;
                    _currentValue = "";
                    continue;
                }

                if (_isHttpVersionRead && c != ':' && c != '\r' && c != '\n')
                {
                    switch(_currentHeaderReadType)
                    {
                        case HEADER_READ_TYPE.READ_KEY:
                            _currentKey += c;
                            break;
                        case HEADER_READ_TYPE.READ_VALUE:
                            _currentValue += c;
                            break;
                    }
                }


                if (_lastReadCharacter != null && _lastReadCharacter == '\r' && c == '\n')
                {
                    if (_isHttpVersionRead)
                    {
                        _currentKey = _currentKey.Trim();
                        _currentValue = _currentValue.Trim();
                        
                        //_currentValue.Remove(2);
                        if (!_headers.ContainsKey(_currentKey) && 
                            !String.IsNullOrWhiteSpace(_currentKey) &&
                            !String.IsNullOrWhiteSpace(_currentValue))
                        {
                            _headers.Add(_currentKey, _currentValue);
                            _currentHeaderReadType = HEADER_READ_TYPE.READ_KEY;
                            _currentKey = "";
                        }
                    }
                    
                    _headerEndCount++;
                    if (_headerEndCount >= 2)
                    {
                        if (_headers.ContainsKey("Content-Length"))
                        {
                            Console.WriteLine("Body: ");
                            Task<int> future2 = Receive(_conn, _buffer, 0, _buffer.Length);
                            future2.ContinueWith(OnReadBody);
                            return;
                        }
                        else
                        {
                            SendHttpResponse(ResponseType.OK);
                            return;
                        }
                    }
                }
                _lastReadCharacter = c;
                Console.Write(c);
            }

            Task<int> future = Receive(_conn, _buffer, 0, _buffer.Length);
            future.ContinueWith(OnBytesReceived);
        }

        private void OnReadBody(Task<int> f)
        {
            long bodyLength = long.Parse(_headers["Content-Length"]);
            if (_isFirstBodyRead)
            {
                _isFirstBodyRead = false;
                _bodyReadLength += _size - _pos;

                if (_size == 0)
                {
                    SendHttpResponse(ResponseType.OK);
                    return;
                }

                while (_pos < _size)
                {
                    byte b = _buffer[_pos];
                    char c = (char)b;
                    ++_pos;
                    Console.Write(c);
                }
                if (_bodyReadLength >= bodyLength)
                {
                    SendHttpResponse(ResponseType.OK);
                    return;
                }

                _size = f.Result;
                _bodyReadLength += _size;
            }
            else
            {
                _size = f.Result;
                _bodyReadLength += _size;
            }

            _pos = 0;

            if (_size == 0)
            {
                SendHttpResponse(ResponseType.OK);
                return;
            }

            while (_pos < _size)
            {
                byte b = _buffer[_pos];
                char c = (char)b;
                ++_pos;
                Console.Write(c);
            }
            if (_bodyReadLength >= bodyLength)
            {
                SendHttpResponse(ResponseType.OK);
                return;
            }

            Task<int> future = Receive(_conn, _buffer, 0, _buffer.Length);
            future.ContinueWith(OnReadBody);
        }

        private void OnSendDone(Task<int> future)
        {
            _conn.Close();
            Console.WriteLine("\nConnection closing ...");
        }

        private static void OnNewConnection(Socket listeningSocket, Task<Socket> f)
		{
            Socket conn = f.Result;
            Console.WriteLine("Connection opened ...");
			Session newSession = new(conn);
			newSession.Start();
            Task<Socket> future = Accept(listeningSocket);
            future.ContinueWith((Task<Socket> f2) => OnNewConnection(listeningSocket, f2));
        }

        public static void Main(string[] args)
        {
			try
			{
				int port = Int32.Parse(args[0]);
				Console.WriteLine("Listening on port: {0} ...", port);
				IPEndPoint listeningEndpoint = new(IPAddress.Any, port);
				using (Socket listeningSocket = new(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Unspecified))
				{
					listeningSocket.Bind(listeningEndpoint);
					listeningSocket.Listen(10);
                    Task<Socket> future = Accept(listeningSocket);
                    future.ContinueWith((Task<Socket> f) => OnNewConnection(listeningSocket, f));
                    while (true)
                    {
                        Thread.Sleep(1000000);
                    }
                }
			}
			catch (Exception ex)
			{
				Console.WriteLine("Exception cought: {0}", ex);
			}
        }

        static Task<Socket> Accept(Socket listeningSocket)
        {
            TaskCompletionSource<Socket> promise = new();
            listeningSocket.BeginAccept((IAsyncResult ar) => promise.SetResult(listeningSocket.EndAccept(ar)), null);
            return promise.Task;
        }

        static Task<int> Receive(Socket conn, byte[] buf, int index, int count)
        {
            TaskCompletionSource<int> promise = new();
            conn.BeginReceive(buf, index, count, SocketFlags.None,
                (IAsyncResult ar) => promise.SetResult(conn.EndReceive(ar)),
                null);
            return promise.Task;
        }

        static Task<int> Send(Socket conn, byte[] buf, int index, int count)
        {
            TaskCompletionSource<int> promise = new();
            conn.BeginSend(buf, index, count, SocketFlags.None,
                (IAsyncResult ar) => promise.SetResult(conn.EndSend(ar)),
                null);
            return promise.Task;
        }
    }
}
