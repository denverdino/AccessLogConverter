
FORMAT_CONFIGS = [
	'default': '%h %{X-Forwarded-For}i %t %P:%p %{tid}P "%m %U ?%q" %>s %b %D',
	'apache': '%h %l %u %t "%r" %>s %b'
]

NON_BLANK_FILEDS = ['%h', '%{X-Forwarded-For}i', '%b', '%D', '%P', '%p', '%m', '%U']

def parseConfigPattern(config, headers = []) {
	
	def regString = config.replaceAll(/\?/, '\\\\?').replaceAll(/%[\S&&[^:"]]+/, { format ->
		headers.add(format);
		if (NON_BLANK_FILEDS.contains(format)) {
			return '(\\S+)';
		} else if ('%t' == format) {
			return '\\[(.+)\\]';
		} else {
			return '(.+)'
		}
	});
	
	//println(regString);

	def pattern = java.util.regex.Pattern.compile(regString);
	return pattern;
}

def parseLine(pattern, line) {
	
	def matcher = pattern.matcher(line);
	def count = matcher.groupCount();
	
	if (matcher.matches()) {
	
		def result = [];
		
		for (int i = 1; i <= count; i++) {		
			result.add(matcher.group(i));
		}		
		return result;	
	} else {	
		System.err.println("Failed to parse: ${line}")
	}	
	return null;
}

def parseLogFile(logFile, cvsFile, config) {
	
	def headers = []
	def pattern = parseConfigPattern(config, headers);
		
	new File (cvsFile).withWriter { writer ->
		//Write header
		writer.writeLine(headers.collect{
			'"' + it + '"'
		}.join(','));
		
		//Write lines
		new File(logFile).eachLine { line ->				
			def fields = parseLine(pattern, line);
			def csvLine = fields.collect {									
				'"' + it.replace('"','""') + '"'					
			}.join(',');
			writer.writeLine(csvLine);
			
		}
	}
	
}



def cli = new CliBuilder(usage: 'groovy log2csv -s "access.log" [-t "access.log.csv"] [-f "format"]')

cli.s(argName:'source', longOpt:'source', args:1, required:true, 'Path of the access.log')
cli.t(argName:'target', longOpt:'target', args:1, required:false, 'Path of the target csv file')
cli.f(argName:'format', longOpt:'format', args:1, required:false, 'Format in the configuration')

def opt = cli.parse(args)

 

if (!opt) {
	System.err.println('Invalid command line');
	return 1;	
}

def source = opt.s;

def target = (opt.t) ?: source + '.csv'; 

def format = FORMAT_CONFIGS[(opt.f) ?: 'default'];

if (format == null) {
	System.err.println("Only support the following format: ${FORMAT_CONFIGS.keySet().join(',')}" );
	return 2;
}

System.out.println("Source file: ${source}")
System.out.println("Target file: ${target}")
System.out.println("Format: ${format}")

parseLogFile(source, target, format);
return 0;

