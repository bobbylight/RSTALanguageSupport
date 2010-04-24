#!/usr/local/bin/perl
#
# create_perl5_xml.pl - Generates the XML API file for RSyntaxTextArea from
#                       perl5_functions.txt
#
# Usage:
#     perl create_perl5_xml.pl
#
use strict;
use Cwd qw(abs_path);
use File::Basename;

my $start = time();

my $this_script = abs_path($0);
my $dir = dirname($this_script);
my $infile = $dir . ($^O =~ m/MSWin32/ ? "\\" : "/") . "perl5.txt";
my $outfile = "$dir/../perl5.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");

# Header information
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>

<!--
   perl5.xml - API specification for the Perl 5.
       Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1

   This file was generated from: $infile
   on date: $datestamp
-->
<api language="Perl">

<environment paramStartChar="(" paramEndChar=")" paramSeparator=", " terminal=";"/>

<keywords>


EOT

open(IN, $infile) || die("Cannot open infile: $!\n");

my @elems;
my $item;
my $definedIn;
my @names;
my $line = <IN>;
while (length($line)>0) {

	# Skip comment lines and empty lines.
	if ($line =~ m/^#.+|^$/) {
		$line = <IN>;
		next;
	}

	if ($line =~ m/^(function|constant) ([\-\w]+)\s*$/) { # An item to add

		my $type = $1;
		my $name = $2;
		my @params;
		my $inDesc = 0;
		my $desc;

		chomp($line = <IN>);
		while ($line !~ m/^-----$/) {
			if ($inDesc==0 and $line =~ m/.+\|/) {
				push(@params, substr($line, 0,-1));
			}
			else {
				$inDesc = 1;
				$desc .= "$line\n";
			}
			chomp($line = <IN>);
		}

		$desc = fixDesc($desc);
		$item = "<keyword name=\"$name\" type=\"$1\">\n";
		$item .= "\t<params>\n";
		foreach my $param (@params) {
			$item .= "\t\t<param name='$param'/>\n";
		}
		$item .= "\t</params>\n";
		$item .= "\t<desc>$desc</desc>\n";
		$item .= "</keyword>";
		#print($item);
		push(@elems, $item);
		$line = <IN>;

	}

	else {
		print(STDERR "ERROR: Unexpected line format: \"$line\"\n");
		exit(1);
	}

}

# Get items for the last header.
if (length($item)>0) {
	push(@elems, $item);
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

my $time = time() - $start;
print "XML generated in $time seconds.\n";



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

