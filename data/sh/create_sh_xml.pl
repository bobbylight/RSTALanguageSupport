#!/usr/local/bin/perl
#
# create_sh_xml.pl - Generates the sh API file for RSyntaxTextArea from sh.txt
#
# Usage:
#     perl create_sh_xml.pl
#
use strict;
use Cwd qw(abs_path);
use File::Basename;


sub fixDesc {

	my $temp = $_[0];

	$temp =~ s/^\s+//;		# Leading whitespace
	$temp =~ s/\n[\n]?$//;	# Final (one or two) newlines
	$temp =~ s!([^>])\n!$1<br>\n!g;	# Newlines (for lines not ending in a tag)

	if ($temp =~ m/[\<\>\&]/) {
		$temp = "<![CDATA[" . $temp . "]]>";
	}
	return $temp;

}


my $this_script = abs_path($0);
my $dir = dirname($this_script);
my $infile = "$dir/sh.txt";
my $outfile = "$dir/../sh.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");

# Header information
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<!DOCTYPE api SYSTEM \"CompletionXml.dtd\">

<!--
   sh.xml - API specification for Unix shells.
           Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1

   This file was generated from: $infile
   on date: $datestamp
-->
<api language="sh">

	<environment paramStartChar="" paramEndChar="" paramSeparator=" " terminal=""/>

	<completionTypes>
		<functionCompletionType type="org.fife.rsta.ac.sh.ShellFunctionCompletion"/>
	</completionTypes>

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

	# Function name
	if ($line =~ m/^([\w\-]+)$/) {

		my $func = $1;
		$item = "\t<keyword name=\"$func\" type=\"function\">\n";
		chomp(my $desc = <IN>);
		$item .= "\t\t<desc>" . fixDesc($desc) . "</desc>\n";
		$item .= "\t</keyword>\n";
		push(@elems, $item);
		$line = <IN>;

	}

	else {
		print(STDERR "ERROR: Unexpected line format: \"$line\"\n");
		exit(1);
	}

}

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
