#!/usr/local/bin/perl
#
# create_php_xml.pl - Generates the XML API file for RSyntaxTextArea from php.txt
#
# Usage:
#     perl create_php_xml.pl
#
use strict;
use Cwd qw(abs_path);
use File::Basename;

my $this_script = abs_path($0);
my $dir = dirname($this_script);
my $infile = "$dir/php.txt";
my $outfile = "$dir/../php.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");

# Header information
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>

<!--
   c.xml - API specification for PHP.
           Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1

   This file was generated from: $infile
   on date: $datestamp
-->
<api language="C">

	<environment paramStartChar="(" paramEndChar=")" paramSeparator=", " terminal=";"/>

	<keywords>


EOT

open(IN, $infile) || die("Cannot open infile: $!\n");

my @elems;
my $item;
my @names;
my $line = <IN>;
while (length($line)>0) {

	# Skip comment lines and empty lines between items.
	if ($line =~ m/^#.+|^$/) {
		$line = <IN>;
		next;
	}

	# A Perl function or "constant" (pre-defined variable).
	if ($line =~ m/^([\w\:\s]+) (function|constant)/) {

		my $name = $1;
		my $returnValDesc;
		push(@names, $name);
		$item = "<keyword name=\"$name\" type=\"$2\"";

		# Functions have a line containing the return type and optional description
		if ($2 eq "function") {
			chomp($line = <IN>);
			if ($line !~ m/^([^\|]+)\|(.*)$/) {
				print("ERROR: Bad format for function return type line: '$line'\n");
				exit(1);
			}
			$item .= " returnType=\"";
			$item .= $1 . "\"";
			$returnValDesc = $2;
		}

		# Close tag and add return value desc tag if necessary.
		$item .= ">\n";
		if (length($returnValDesc)>0) {
			$item .= "\t<returnValDesc>" . fixDesc($returnValDesc) . "</returnValDesc>\n";
		}

		# Loop through any parameters.  Parameters are 1 per line, of the form:
		#    int foo|optional description
		# e.g. "'type' 'name'|'description of this parameter'
		# Overloaded functions can specify "optional" parameters by using
		# '[' and ']':
		#    [ int foo|How many foo's to loop over
		# Leading square braces are scanned for; trailing square braces are
		# discarded (it is assumed that they are paired, but it isn't necessary).
		my @params;
		while (chomp($line=<IN>) && ($line =~ m/^[^ ]/)) {

			my $param = "";

			# Many slightly different variations of param lines in the text
			# file.  These are mostly copied straight from create_c_xml.pl.

			if ($line =~ m/^(\w+) (\([^\)]+\)\([^\)]+\))\|(.*)$/) { # C - bsearch - tricky function argument
				$param .= "\t\t<param type=\"" . $1 . "\" name=\"" . $2 . "\"";
				if (length($3)>0) {
					# Try to only put param descs in CDATA if necessary, to keep XML size down.
					my $desc = $3;
					if ($desc =~ m/[<>]/) {
						$desc = fixDesc($desc);
					}
					$param .= ">\n\t\t\t<desc>$desc</desc>\n\t\t</param>\n";
				}
				else {
					$param .= "/>\n";
				}
			}

#			elsif ($line =~ m/^(.+)? ([\w_\(\)\*]+(:?\[[\w_]+\])?)\|(.*)$/) {
elsif ($line =~ m/^(\s*\[?\s*[^ ]+) ([\w_&\$\.]+\s*\]*\s*)\|(.*)$/) {
#[ int level]|
				$param .= "\t\t<param type=\"" . $1 . "\" name=\"" . $2 . "\"";
				if (length($4)>0) {
					# Try to only put param descs in CDATA if necessary, to keep XML size down.
					my $desc = $4;
					if ($desc =~ m/[<>]/) {
						$desc = fixDesc($desc);
					}
					$param .= ">\n\t\t\t<desc>$desc</desc>\n\t\t</param>\n";
				}
				else {
					$param .= "/>\n";
				}
			}

			elsif ($line =~ m/^\.\.\.\|(.*)$/) {
				$param .= "\t\t<param name=\"...\"";
				if (length($1)>0) {
					# Try to only put param descs in CDATA if necessary, to keep XML size down.
					my $desc = $1;
					if ($desc =~ m/[<>]/) {
						$desc = fixDesc($desc);
					}
					$param .= ">\n\t\t\t<desc>$desc</desc>\n\t\t</param>\n";
				}
				else {
					$param .= "/>\n";
				}
			}

			elsif ($line =~ m/^([\w_])+\|(.*)?$/) {
				$param .= "\t\t<param type=\"" . $1 . "\"";
				if (length($2)>0) {
					# Try to only put param descs in CDATA if necessary, to keep XML size down.
					my $desc = $2;
					if ($desc =~ m/[<>]/) {
						$desc = fixDesc($desc);
					}
					$param .= ">\n\t\t\t<desc>$desc</desc>\n\t\t</param>\n";
				}
				else {
					$param .= "/>\n";
				}
			}

			else {
				print("WARNING: Param line didn't match regex:\n");
				print("\"$line\"\n");

			}

			# Strip the param of the '[' and ']' brackets denoting optional
			# arguments.
			if ($param =~ m/type="\s*\[\s*/) {
				$param =~ s/type="\s*\[\s*/type="/;
				$param = "@" . $param; # prefix with '@' so we know
			}

			# Strip trailing ']' chars, and escape '&' chars, from names.
			$param =~ s/\s*\]*\s*" name=$/" name=/;
			$param =~ s/&/&amp;/g;
			push(@params, $param);

		}

		# Add a marker for parameters.  "$item" will be treated as a template
		# for all overloads of this function.
		if (@params>0) {
			$item .= "\t<params>\n";
			$item .= "!param_marker!";
			$item .= "\t</params>\n";
		}

		# Collect the "description" of the function.  This is one or more
		# lines starting with ' '.  Empty lines count (can be used for spacing
		# in the description).  This is an optional field and may not exist
		# for any particular function in the text file.  A description that
		# ends up being only empty lines is assumed to be spacing in the text
		# file and is discarded.
		my $desc = "";
		while (defined($line) && ($line =~ m/^$|^ /)) {
			$desc .= substr($line, 1) . "\n";
			chomp($line = <IN>);
		}
		$desc = fixDesc($desc);
		if (length($desc)>0) {
			$item .= "\t<desc>";
			$item .= "$desc</desc>\n";
		}
		$item .= "</keyword>";

		# Using "$item" as a template, create a Completion for each overload
		# of this function.
		if (@params==0) {
			push(@elems, $item);
		}
		else {
			my $paramStr = "";
			foreach my $param (@params) {
				my $temp = $item;
				if ($param =~ m/^@/) { # Start of an optional param
					 $temp =~ s/!param_marker!/$paramStr/;
					 push(@elems, $temp);
					 $paramStr .= substr($param, 1);
				}
				else {
					$paramStr .= $param;
				}
			}
			$item =~ s/!param_marker!/$paramStr/;
			push(@elems, $item); # Last one
		}
	}

	else {
		print(STDERR "ERROR: Unexpected line format: \"$line\"\n");
		exit(1);
	}

}

## Get items for the last header.
#if (length($item)>0) {
#	push(@elems, $item);
#}

if (@elems>0) {
	foreach (sort {lc $a cmp lc $b} @elems) {
		my $elem = $_;
		print(OUT "$elem\n");
	}
}

close(IN);

# Print footer of XML definition file
print OUT <<EOT;
	</keywords>

</api>
EOT


sub fixDesc() {

	my $temp = $_[0];

	$temp =~ s/^\s+//;		# Leading whitespace
	$temp =~ s/\n[\n]?$//;	# Final (one or two) newlines
	$temp =~ s!([^>])\n!$1<br>\n!g;	# Newlines (for lines not ending in a tag)

	if ($temp =~ m/[\<\>\&]/) {
		$temp = "<![CDATA[" . $temp . "]]>";
	}
	return $temp;

}

