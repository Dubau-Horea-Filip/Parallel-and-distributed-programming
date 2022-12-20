using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace FC_EventDriven_PDP
{
    internal class Client
    {
        public static void Main(string[] args)
        {
            try
            {
                string serverName = args[0];
                IPHostEntry hostEntry = Dns.GetHostEntry(serverName);
                IPAddress serverIp = null;
                foreach (IPAddress ip in hostEntry.AddressList)
                {
                    if (ip.AddressFamily == AddressFamily.InterNetwork)
                    {
                        serverIp = ip;
                    }
                }
                Console.WriteLine("IP={0}", serverIp);
                int serverPort = int.Parse(args[1]);
                Socket conn = new Socket(SocketType.Stream, ProtocolType.Tcp);
                conn.Connect(serverIp, serverPort);
                while (true)
                {

                }
                conn.Close();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception caught: {0}", ex);
            }
        }
    }
}
