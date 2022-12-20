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
            _conn.BeginReceive(_buffer, 0, _buffer.Length, SocketFlags.None, OnBytesReceived, null);
        }

        private void OnBytesReceived(IAsyncResult ar)
        {
            _size = _conn.EndReceive(ar);
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

            _conn.BeginSend(b, 0, b.Length, SocketFlags.None, OnSendDone, null);
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
                            _conn.BeginReceive(
                                _buffer, 
                                0, 
                                _buffer.Length, 
                                SocketFlags.None,
                                OnReadBody,
                                null
                            );
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

            _conn.BeginReceive(_buffer, 0, _buffer.Length, SocketFlags.None, OnBytesReceived, null);
        }

        private void OnReadBody(IAsyncResult ar)
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

                _size = _conn.EndReceive(ar);
                _bodyReadLength += _size;
            }
            else
            {
                _size = _conn.EndReceive(ar);
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

            _conn.BeginReceive(
                _buffer,
                0,
                _buffer.Length,
                SocketFlags.None,
                OnReadBody,
                null
            );
        }

        private void OnSendDone(IAsyncResult ar)
        {
            _conn.EndSend(ar);
            _conn.Close();
        }

        private static void OnNewConnection(Socket listeningSocket, IAsyncResult ar)
		{
			Socket conn = listeningSocket.EndAccept(ar);
			Console.WriteLine("Connection opened ...");
			Session newSession = new(conn);
			newSession.Start();
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
                    listeningSocket.BeginAccept((IAsyncResult ar) => OnNewConnection(listeningSocket, ar), null);
                    while (true)
                    {
                        
                    }
				}
			}
			catch (Exception ex)
			{
				Console.WriteLine("Exception cought: {0}", ex);
			}
        }
    }
}
