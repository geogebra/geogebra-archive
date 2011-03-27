#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# This script creates the portable version for Linux (32bit & 64bit).
# @author Christian Schött <schoett@gmx.de>

# argument 1: version of GeoGebra (eg. 3.2.44.0)
# argument 2: path of executable jre installation file (eg. jre-6u21-linux-x64.bin)
# argument 3: path of directory containing unpacked geogebra files
# argument 4: path of directory containing unsigned geogebra files
# argument 5: path of geogebra icon (eg. geogebra.png)
# argument 6: path of start script geogebra
# argument 7: path of destination directory

import os, shutil, sys, tarfile, tempfile
if len(sys.argv) != 8:
	print("Error: Seven arguments are expected.")
	sys.exit(1)
if not os.path.exists(sys.argv[2]):
	print("Error: "+sys.argv[2]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[3]):
	print("Error: "+sys.argv[3]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[4]):
	print("Error: "+sys.argv[4]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[5]):
	print("Error: "+sys.argv[5]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[6]):
	print("Error: "+sys.argv[6]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[7]):
	print("Error: "+sys.argv[7]+" does not exist.")
	sys.exit(1)
geogebra_version = sys.argv[1].replace(".", "-")
java_path = os.path.abspath(sys.argv[2])
unpacked_path = os.path.abspath(sys.argv[3])
unsigned_path = os.path.abspath(sys.argv[4])
icon_path = os.path.abspath(sys.argv[5])
start_script_path = os.path.abspath(sys.argv[6])
destination_path = os.path.abspath(sys.argv[7])
if not os.path.isfile(java_path):
	print("Error: "+java_path+" is not a file.")
	sys.exit(1)
if not os.access(java_path,os.X_OK):
	print("Error: "+java_path+" is not executable.")
	sys.exit(1)
if not os.path.isdir(unpacked_path):
	print("Error: "+unpacked_path+" is not a directory.")
	sys.exit(1)
if not os.path.isdir(unsigned_path):
	print("Error: "+unsigned_path+" is not a directory.")
	sys.exit(1)
if not os.path.isfile(icon_path):
	print("Error: "+icon_path+" is not a file.")
	sys.exit(1)
if os.path.splitext(icon_path)[1] != ".png":
	print("Error: "+icon_path+" has not the ending \".png\".")
	sys.exit(1)
if not os.access(icon_path,os.R_OK):
	print("Error: "+icon_path+" is not readable.")
	sys.exit(1)
if not os.path.isfile(start_script_path):
	print("Error: "+start_script_path+" is not a file.")
	sys.exit(1)
if not os.path.isdir(destination_path):
	print("Error: "+destination_path+" is not a directory.")
	sys.exit(1)
java_filename = os.path.basename(java_path)
icon_filename = os.path.basename(icon_path)
arch = ""
if "linux-x64" in java_filename:
	arch = "64bit-"
elif not "linux-i586" in java_filename:
	print("Error: Architecture can not be determined.")
	sys.exit(1)
temp_dir = tempfile.mkdtemp()
try:
	shutil.copytree(unpacked_path, temp_dir+"/geogebra")
	os.chdir(temp_dir+"/geogebra")
	shutil.copytree(unsigned_path, "unsigned")
	shutil.copy(icon_path, ".")
	if not icon_filename == "geogebra.png":
		os.rename(icon_filename, "geogebra.png")
	os.chmod("geogebra.png",0o444)
	shutil.copy(start_script_path, ".")
	os.chmod("geogebra",0o755)
	os.system("sh "+java_path)
	content = os.listdir(os.getcwd())
	jre_dir = ""
	for name in content:
		if "jre" in name:
			jre_dir = name
			break
	if jre_dir == "":
		print("Error: "+java_filename+" does not create expected folder.")
		sys.exit(1)
	os.rename(jre_dir, "jre")
	os.chdir("../")
	tar_gz_file = tarfile.open(destination_path+"/GeoGebra-Linux-"+arch+"Portable-"+geogebra_version+".tar.gz", "w:gz")
	try:
		tar_gz_file.add("geogebra")
	finally:
		tar_gz_file.close()
finally:
	shutil.rmtree(temp_dir)
sys.exit(0)