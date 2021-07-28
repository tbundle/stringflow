#!/bin/bash

USR=$1

if [ $# -ne 1 ]; then
   echo "Usage: build-server.sh <git user>"
   exit
fi

# Define stringflow home dir
SF_HOME=stringflow

# Create root directory with name stringflow. Delete if there 
# is an existing directory first.
echo "Executing stringflow build script from `pwd`"
rm -rf $SF_HOME
if [ $? -ne 0 ]; then
   echo "Unable to cleanup exisitng dir; exiting..."
   exit
fi

mkdir $SF_HOME
chmod -R 777 $SF_HOME

if [ $? -ne 0 ]; then
   echo "Not enough permissions;quitting..."
   exit
fi

cd "$SF_HOME"
echo "Creating install dirs in `pwd`"
mkdir libs bin conf logs ext store deploy temp
cd temp

BASE_GIT_URL=142.93.208.62:/opt/git/sf-server.git
FULL_GIT_URL="$USR@$BASE_GIT_URL"
echo "Cloning Git repository..."
git clone $FULL_GIT_URL

if [ $? -ne 0 ]; then
    echo "git clone failed; bye."
    exit
fi

# Copy bin files to installation bin directory
cd sf-server
echo "Copying bin files to bin dir..."
cp -r ixi/bin/* ../../bin/

echo "------------------------------------"
echo "     Performing maven build         "
echo "------------------------------------"

# Build quick-http
cd quick-http
mvn clean install
if [ $? -ne 0 ]; then
   echo "Maven build failed; exiting"
   exit
fi

# Building server
cd ../ixi
mvn clean install

if [ $? -ne 0 ]; then
   echo "Maven build failed; exiting"
   exit
fi

# Copy liberaries to libs
echo "Copying libraries to lib directory..."
cd ../../../
rm -rf ./libs/*
tar -xvf temp/sf-server/ixi/target/stringflow-*dependencies.tar -C ./libs/

# putting confs to conf
rm -rf  ./conf/*
cp ./temp/sf-server/ixi/target/conf/* ./conf/

# deleting source code
rm -rf ./temp/*

# giving permissions 
echo "Granting permissions..."
chmod 555 -R libs
chmod 555 -R bin 
chmod 555 -R conf
chmod 777 -R logs
chmod 777 -R ext
chmod 777 -R store
chmod 777 -R deploy
chmod 777 -R temp

# creating stringflow tar
echo "Exporting Stringflow exutable..."
cd ..
tar -zcvf stringflow.tar.gz stringflow/

# deleting stringflow-tmp
echo "Cleaning-up file system..."
rm -rf stringflow

echo "--------------------------------------------"
echo "        Stringflow Build is Successful      "
echo "--------------------------------------------"
