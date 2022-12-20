using Lab4.Implementation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lab4
{
    class Program
    {
        static void Main(string[] args)
        {
            var hosts = new string[]
            {   "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/",
                "motogna.wordpress.com/courses/",
                "www.cs.ubbcluj.ro/~dan"
            }.ToList();
            //Wrap the connect/send/receive operations in tasks, with the callback setting the result of the task; ->false
            //Like the previous, but also use the async/await mechanism. -> true
            //TaskImplementation.run(hosts, true);
            
            
             CallBackImplementation.run(hosts);
       
        }
    }
}