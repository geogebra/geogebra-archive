#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# This script creates the generic version for Linux.
# @author Christian Schött <schoett@gmx.de>

# argument 1: version of GeoGebra (eg. 3.2.44.0)
# argument 2: path of directory containing unpacked geogebra files
# argument 3: path of directory containing unsigned geogebra files
# argument 4: path of start script geogebra
# argument 5: path of file license.txt
# argument 6: path of file geogebra.xml
# argument 7: path of file geogebra.desktop
# argument 8: path of file GeoGebra_hicolor_icons.tar.gz
# argument 9: path of destination directory

import os, shutil, sys, tarfile, tempfile
if len(sys.argv) != 10:
	print("Error: Nine arguments are expected.")
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
if not os.path.exists(sys.argv[9]):
	print("Error: "+sys.argv[9]+" does not exist.")
	sys.exit(1)
geogebra_version = sys.argv[1]
unpacked_path = os.path.abspath(sys.argv[2])
unsigned_path = os.path.abspath(sys.argv[3])
start_script_path = os.path.abspath(sys.argv[4])
license_txt_path = os.path.abspath(sys.argv[5])
geogebra_xml_path = os.path.abspath(sys.argv[6])
geogebra_desktop_path = os.path.abspath(sys.argv[7])
icons_tar_gz_file_path = os.path.abspath(sys.argv[8])
destination_path = os.path.abspath(sys.argv[9])
if not os.path.isdir(unpacked_path):
	print("Error: "+unpacked_path+" is not a directory.")
	sys.exit(1)
if not os.path.isdir(unsigned_path):
	print("Error: "+unsigned_path+" is not a directory.")
	sys.exit(1)
if not os.path.isfile(start_script_path):
	print("Error: "+start_script_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(license_txt_path):
	print("Error: "+license_txt_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(geogebra_xml_path):
	print("Error: "+geogebra_xml_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(geogebra_desktop_path):
	print("Error: "+geogebra_desktop_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(icons_tar_gz_file_path):
	print("Error: "+icons_tar_gz_file_path+" is not a file.")
	sys.exit(1)
if not os.path.isdir(destination_path):
	print("Error: "+destination_path+" is not a directory.")
	sys.exit(1)
temp_dir = tempfile.mkdtemp()
try:
	os.chdir(temp_dir)
	os.mkdir("geogebra-"+geogebra_version)
	for element in os.listdir(unpacked_path):
		if os.path.isfile(unpacked_path+"/"+element) and os.path.splitext(unpacked_path+"/"+element)[1] == ".jar":
			shutil.copy(unpacked_path+"/"+element, "geogebra-"+geogebra_version)
	os.chdir("geogebra-"+geogebra_version)
	os.mkdir("unsigned")
	for element in os.listdir(unsigned_path):
		if os.path.isfile(unsigned_path+"/"+element) and os.path.splitext(unsigned_path+"/"+element)[1] == ".jar":
			shutil.copy(unsigned_path+"/"+element, "unsigned")
	os.mkdir("icons")
	icons_tar_gz_file = tarfile.open(icons_tar_gz_file_path, "r:gz")
	try:
		icons_tar_gz_file.extractall("icons")
	finally:
		icons_tar_gz_file.close()
	shutil.copy(start_script_path, ".")
	os.chmod("geogebra",0o755)
	shutil.copy(license_txt_path, ".")
	shutil.copy(geogebra_xml_path, ".")
	shutil.copy(geogebra_desktop_path, ".")
	os.chdir(temp_dir)
	geogebra_tar_gz_file = tarfile.open(destination_path+"/geogebra-"+geogebra_version+".tar.gz", "w:gz")
	try:
		geogebra_tar_gz_file.add("geogebra-"+geogebra_version)
	finally:
		geogebra_tar_gz_file.close()
finally:
	shutil.rmtree(temp_dir)
sys.exit(0)