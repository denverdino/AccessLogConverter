# Description
AccessLogConverter
==================
Convert the access log to .csv format for analyzing with Excel and OpenOffice. 

# Requirement
* Groovy is installed and configured.
		http://groovy.codehaus.org/
		
# Usage

	groovy log2csv.groovy -s "access.log" [-t "access.log.csv"] [-f "format"]
		-f,--format <format>   Format defined in the configuration, the default value is "default"
		-s,--source <source>   Path of the access.log
		-t,--target <target>   Path of the target csv file



Tips:

a) How to support the customized access.log format?
Add/Modify the configurations defined in the log2csv.groovy


	FORMAT_CONFIGS = [
	'default': '%h %{X-Forwarded-For}i %t %P:%p %{tid}P "%m %U ?%q" %>s %b %D',
	'apache': '%h %l %u %t "%r" %>s %b'
	]
	
b) How to control the parser to split the log entry properly?

You can add some fields as the non-blank fields in the log2csv.groovy. 

	NON_BLANK_FILEDS = ['%h', '%{X-Forwarded-For}i', '%b', '%D', '%P', '%p', '%m', '%U']

Reference:
http://httpd.apache.org/docs/2.2/mod/mod_log_config.html
