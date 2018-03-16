#!/bin/bash

sudo service tomcat8 stop
cd /var/lib/tomcat8/webapps
sudo rm -rf ROOT
sudo service tomcat8 start
