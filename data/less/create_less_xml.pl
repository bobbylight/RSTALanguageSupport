#!/usr/local/bin/perl
#
# create_less_xml.pl - Generates the XML representing all Less built-in functions.
#
# Usage:
#     perl create_less_xml.pl
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
my $infile = "$dir/less_builtin_functions.txt";
my $outfile = "$dir/../less_functions.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");

# Header information
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<!DOCTYPE api SYSTEM \"CompletionXml.dtd\">

<!--
   less.xml - Builtin functions of Less.
           Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1
   
   This file was generated from: $infile
   on date: $datestamp
-->
<api language="Less">

	<environment paramStartChar="(" paramEndChar=")" paramSeparator=", " terminal=";"/>

	<keywords>


EOT

open(IN, $infile) || die("Cannot open infile: $!\n");

my @elems;
my $item;
my $definedIn;
my @names;
my $line = <IN>;
while (defined($line)) {#length($line)>0) {

	# Skip header lines and empty lines between items.
	if ($line =~ m/^#+\s+([^ ]+)\s+#+$/) {
		$definedIn = $1;
		$line = <IN>;
		next;
	}
	elsif ($line =~ m/^#.+|^ *$/) {
		$line = <IN>;
		next;
	}
   
   if ($line =~ m/^name: ([\w\-\%]+)/) { # An item to add
      
      $item = "<keyword name='$1' type='function'>\n";
      
      my $desc;
      chomp($line = <IN>);
      if ($line !~ m/^desc: /) {
         print("ERROR: Expected desc line; found '$line'\n");
         exit(1);
      }
      $desc = substr($line, 6);
      while (chomp($line=<IN>) && ($line =~ m/^ /)) {
         $desc .= "\n$line";
      }
      $item .= "\t<desc>" . fixDesc($desc) . "</desc>\n";
      
      my $params = "";
      while ($line =~ /^param: ([\w\-\, \.]+): (.+)$/) {
         my $paramName = $1;
         my $paramDesc = $2;
         while (chomp($line = <IN>) && $line =~ /^   /) {
            $paramDesc .= "\n" . substr($line, 2);
            chomp($line = <IN>);
         }
         $params .= "\t\t<param name=\"$paramName\">\n";
         $params .= "\t\t\t<desc>" . fixDesc($paramDesc) . "</desc>\n";
         $params .= "\t\t</param>\n";
      }
      $item .= "\t<params>\n$params\t</params>\n";
      
      if ($line =~ /^Returns: /) {
         my $returnValueDesc = substr($line, 9);
         $item .= "\t<returnValDesc>" . fixDesc($returnValueDesc) . "</returnValDesc>\n";
         chomp($line = <IN>);
      }
      
      $item .= "</keyword>\n";
      
      if ($line =~ /^Added in: (.+)$/) {
         my $definedIn = $1;
         # TODO: Add $definedIn as attribute on <keyword/> tag
         $item = substr($item, 0, length("<keyword ")) .
               "definedIn=\"$definedIn\" " . substr($item, length("<keyword "));
         chomp($line = <IN>);
      }
   }
   
	else {
		print(STDERR "ERROR: 'name' line didn't match regex:\n");
		print("\"$line\"\n");
      print("Current item: $item");
      exit(2);
	}
   
	#print($item);
	push(@elems, $item);
 
}

# Get items for the last header.
if (length($item)>0) {
	push(@elems, $item);
}

if (@elems>0) {
	foreach (sort {lc $a cmp lc $b} @elems) {
		print(OUT "$_\n");
	}
}

close(IN);

# Print footer of XML definition file
print OUT <<EOT;
	</keywords>

</api>
EOT
