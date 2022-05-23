# MCPlayerReputationDatabase
MC Player Reputation Database

For Windows use only, because the CLIAPI in exe format is used.
In other words, if api files can be allowed to run on Linux systems or other systems, 
then mrdb can also run on Linux systems or other systems.

The server I used for test was a 1.18.1 Arclight server, which indicates that 
the plugin is compatible with 1.12.2-1.18.1 server versions, 
but it is theoretically compatible with all existing versions higher than 1.12.2.(Not tested)

Used CLIapi:
  https://github.com/RimoOvO/OpenMPRDB-Python-CLI
  
  Since the api does not give a way to change the remote server, 
  this plugin does not support changing the remote server for the time being.
  
  When using the api, its contents are modified as follows:
  1.Removed everything that required manual typing for the next step.
  2.The python file is packaged to make it an exe file.

Doc:
  https://docs.qq.com/doc/DUFNNV2pFckpxRVR3
  
  The actual run result may be different from what is described in the doc! 
  Please refer to the content of the help page.

Usage:
  Put the release into the plugin folder of your server, run the server, and use /PRDB for help.
  
  When you first run the PRDB, it automatically initializes and copies the files, which is a 
  synchronous operation that may take up to 2 minutes (depending on server performance).
  
  The first run of the PRDB will always report a file read failure error, this is because 
  you have not yet written your server information, please write the required server information 
  as prompted, and reload the server.
  
  If you need to submit, make sure that the server has genuine verification turned on, 
  otherwise any form of submit submission will not pass.
