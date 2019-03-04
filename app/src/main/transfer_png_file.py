# coding=utf-8

import os, sys

reload(sys)
sys.setdefaultencoding('utf8') 
__author__ = 'songjunmin'

IMAGE_FILE_ROOT = '/Users/songjunmin/Desktop/sss'

def list_all_files(rootdir):
	
	_files = []
	_list = os.listdir(rootdir) #列出文件夹下所有的目录与文件
	for i in range(0, len(_list)):
		path = os.path.join(rootdir,_list[i])
		if os.path.isdir(path):
			_files.extend(list_all_files(path))
		if os.path.isfile(path) and (path.endswith('.png') or path.endswith('.jpg')):
			_files.append(path)
	return _files


if __name__ == '__main__':
	image_files = list_all_files(IMAGE_FILE_ROOT)
	if len(image_files) > 0:
		for img in image_files:
			file_name_index = img.rfind('/') + 1
			new_name = 'pipeline_' + img[file_name_index:].lower()

			os.system("cp " + img + ' ' + IMAGE_FILE_ROOT + '/' + new_name)