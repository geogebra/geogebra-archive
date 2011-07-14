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
# argument 7: path of Maxima installation file (eg. maxima-5.24.0.tar.gz)
# argument 8: path of destination directory

import os, shutil, sys, tarfile, tempfile
if len(sys.argv) != 9:
	print("Error: Eight arguments are expected.")
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
if not os.path.exists(sys.argv[8]):
	print("Error: "+sys.argv[8]+" does not exist.")
	sys.exit(1)
geogebra_version = sys.argv[1]
java_path = os.path.abspath(sys.argv[2])
unpacked_path = os.path.abspath(sys.argv[3])
unsigned_path = os.path.abspath(sys.argv[4])
icon_path = os.path.abspath(sys.argv[5])
start_script_path = os.path.abspath(sys.argv[6])
maxima_tar_gz_file_path = os.path.abspath(sys.argv[7])
destination_path = os.path.abspath(sys.argv[8])
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
if not os.path.isfile(maxima_tar_gz_file_path):
	print("Error: "+maxima_tar_gz_file_path+" is not a file.")
	sys.exit(1)
if not os.path.isdir(destination_path):
	print("Error: "+destination_path+" is not a directory.")
	sys.exit(1)
java_filename = os.path.basename(java_path)
icon_filename = os.path.basename(icon_path)
maxima_tar_gz_filename = os.path.basename(maxima_tar_gz_file_path)
arch = "i586"
if "linux-x64" in java_filename:
	arch = "x86_64"
elif not "linux-i586" in java_filename:
	print("Error: Architecture can not be determined.")
	sys.exit(1)
temp_dir = tempfile.mkdtemp()
try:
	shutil.copytree(unpacked_path, temp_dir+"/GeoGebra-Linux-"+arch+"-Portable-"+geogebra_version)
	os.chdir(temp_dir+"/GeoGebra-Linux-"+arch+"-Portable-"+geogebra_version)
	shutil.copytree(unsigned_path, "unsigned")
	shutil.copy(icon_path, ".")
	if not icon_filename == "geogebra.png":
		os.rename(icon_filename, "geogebra.png")
	os.chmod("geogebra.png",0o444)
	shutil.copy(start_script_path, ".")
	os.chmod("geogebra",0o755)
	os.system("sh "+java_path)
	maxima_tar_gz_file = tarfile.open(maxima_tar_gz_file_path, "r:gz")
	try:
		maxima_tar_gz_file.extractall()
	finally:
		maxima_tar_gz_file.close()
	content = os.listdir(os.getcwd())
	jre_dir = ""
	for name in content:
		if "jre" in name:
			jre_dir = name
			break
	if jre_dir == "":
		print("Error: "+java_filename+" does not create expected folder.")
		sys.exit(1)
	maxima_dir = ""
	for name in content:
		if "maxima" in name:
			maxima_dir = name
			break
	if maxima_dir == "":
		print("Error: "+maxima_tar_gz_filename+" does not create expected folder.")
		sys.exit(1)
	with open("geogebra-portable-mathpiper", "w") as start_script_geogebra_portable_file:
		start_script_geogebra_portable_file_lines = ["#!/bin/bash\n", "#---------------------------------------------\n", "# Script to start GeoGebra-Portable with MathPiper as CAS engine\n", "#---------------------------------------------\n", "\n", "#---------------------------------------------\n", "# Export name of this script\n", "\n", "export GG_SCRIPTNAME=$(basename $0)\n", "\n", "#---------------------------------------------\n", "# Find out path of this script\n", "\n", "GG_PATH=\"${BASH_SOURCE[0]}\"\n", "if [ -h \"${GG_PATH}\" ]; then\n", "\twhile [ -h \"${GG_PATH}\" ]; do\n", "\tGG_PATH=`readlink \"${GG_PATH}\"`\n", "\tdone\n", "fi\n", "pushd . > /dev/null\n", "cd `dirname ${GG_PATH}` > /dev/null\n", "GG_PATH=`pwd`\n", "popd > /dev/null\n", "\n", "#---------------------------------------------\n", "# Export Java Command\n", "\n", "export JAVACMD=\"$GG_PATH/"+jre_dir+"/bin/java\"\n", "\n", "#---------------------------------------------\n", "# Run\n", "\n", "exec \"$GG_PATH/geogebra\" --settingsfile=\"$GG_PATH/geogebra.properties\" --CAS=MathPiper \"$@\"\n"]
		start_script_geogebra_portable_file.writelines(start_script_geogebra_portable_file_lines)
	os.chmod("geogebra-portable-mathpiper",0o755)
	with open("geogebra-portable-maxima", "w") as start_script_geogebra_portable_file:
		start_script_geogebra_portable_file_lines = ["#!/bin/bash\n", "#---------------------------------------------\n", "# Script to start GeoGebra-Portable with Maxima as CAS engine\n", "#---------------------------------------------\n", "\n", "#---------------------------------------------\n", "# Export name of this script\n", "\n", "export GG_SCRIPTNAME=$(basename $0)\n", "\n", "#---------------------------------------------\n", "# Find out path of this script\n", "\n", "GG_PATH=\"${BASH_SOURCE[0]}\"\n", "if [ -h \"${GG_PATH}\" ]; then\n", "\twhile [ -h \"${GG_PATH}\" ]; do\n", "\tGG_PATH=`readlink \"${GG_PATH}\"`\n", "\tdone\n", "fi\n", "pushd . > /dev/null\n", "cd `dirname ${GG_PATH}` > /dev/null\n", "GG_PATH=`pwd`\n", "popd > /dev/null\n", "\n", "#---------------------------------------------\n", "# Export Java Command\n", "\n", "export JAVACMD=\"$GG_PATH/"+jre_dir+"/bin/java\"\n", "\n", "#---------------------------------------------\n", "# Run\n", "\n", "exec \"$GG_PATH/geogebra\" --settingsfile=\"$GG_PATH/geogebra.properties\" --CAS=Maxima --maximaPath=\"$GG_PATH/"+maxima_dir+"/usr/local/bin/maxima\" \"$@\"\n"]
		start_script_geogebra_portable_file.writelines(start_script_geogebra_portable_file_lines)
	os.chmod("geogebra-portable-maxima",0o755)
	with open("readme.txt", "w") as readme_file:
		readme_file_lines = ["To start GeoGebra-Portable with MathPiper as CAS engine, run geogebra-portable-mathpiper.\n", "To start GeoGebra-Portable with Maxima as CAS engine, run geogebra-portable-maxima.\n"]
		readme_file.writelines(readme_file_lines)
	os.chdir("../")
	tar_gz_file = tarfile.open(destination_path+"/GeoGebra-Linux-"+arch+"-Portable-"+geogebra_version+".tar.gz", "w:gz")
	try:
		tar_gz_file.add("GeoGebra-Linux-"+arch+"-Portable-"+geogebra_version)
	finally:
		tar_gz_file.close()
finally:
	shutil.rmtree(temp_dir)
sys.exit(0)