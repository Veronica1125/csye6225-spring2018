#!/bin/bash

sudo cp /home/ubuntu/awslogs.conf /var/awslogs/etc/
sudo service awslogs start
sudo service awslogs restart
