#!/usr/local/bin/perl
#
# create_jsp_xml.pl - Generates the JSP API file for RSyntaxTextArea.
#
# Usage:
#     perl create_html_xml.pl
#
use strict;
use Cwd qw(abs_path);
use List::Util qw[min max];
use File::Basename;

my $this_script = abs_path($0);
my $dir = dirname($this_script);
my $outfile = "$dir/../jsp.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");


# Print header of XML definition file
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<!DOCTYPE api SYSTEM \"CompletionXml.dtd\">

<!--

   jsp.xml - API specification for standard JSP tags.
             Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1

   This file was generated from: $this_script
   on date: $datestamp

 -->
<api language="JSP">
<!--
	<environment paramStartChar="(" paramEndChar=")" paramSeparator=", " terminal=";" />
-->

<keywords>
EOT


# Create mapping of tags to descriptions, and tags to supported HTML versions
my %tagToDesc;

# create mapping of tags to their attribuets, mapping of attributes to their
# descriptions, etc.
my %tagsToAttributes;
my %attrsToDesc;
my %attrsToType;

open(INFILE, "jsp_input.txt") || die("Cannot open INFILE: $!\n");
my $curTag;
my $curAttr;
my $curAttrDesc;
my $inPre = 0;
my $inList = 0; # Either ol or ul (assuming there are no nested lists)
my $lastLineWasEmpty = 0;
my $line;

while (chomp($line = <INFILE>)) {

	$line =~ s/^\s+//g;
	$line =~ s/\s+$//g;
	my $firstChar = substr($line, 0, 1);

	# If we're starting a new tag definition
	if ($firstChar eq "*") {
		if (defined($curAttr)) {
			$attrsToDesc{$curAttr} = $curAttrDesc;
			push(@{$tagsToAttributes{$curTag}}, $curAttr);
		}
		elsif (defined($curAttrDesc)) { # We're actually collecting a tag's desc
			$tagToDesc{$curTag} = $curAttrDesc;
		}
		$curTag = $line;
		$curTag =~ s/\*?\s+//g;
		$curAttr = $curAttrDesc = undef;
		$inPre = 0;
		$inList = 0;
	}
	
	# Starting a new attribute
	elsif ($firstChar eq "-") {
		if (defined($curAttr)) {
			$attrsToDesc{$curAttr} = $curAttrDesc;
			push(@{$tagsToAttributes{$curTag}}, $curAttr);
		}
		elsif (defined($curAttrDesc)) { # We're actually collecting a tag's desc
			$tagToDesc{$curTag} = $curAttrDesc;
		}
		$curAttrDesc = undef;
		$line =~ s/^\s*\-?\s+//;
my @tokens = split(/\s+/, $line);
$curAttr = $tokens[0];
my $required = $tokens[1];
if (@tokens>2) {
	my $type = $tokens[2];
	#$type =~ s/[\[\]]//g;  # Remove leading '[' and trailing ']'
	$type =~ s/\|/, /g; # Convert dividing '|' to ', '
	$type =~ s/\*([\w\-]+)/&lt;b&gt;$1&lt;\/b&gt;/; # Convert default values, i.e. "*true", to "<b>true</b>"
	$attrsToType{$curAttr} = $type;
}
		$curAttr =~ s/\-?\s+//g;
		$inPre = 0;
		$inList = 0;
	}

	# Any other character is a description
	else {
		next if $line =~ /^<\/?p>$/; # Skip lines that are just paragraph elements
		my $lastPreOpen = rindex($line, "<pre>");
		my $lastPreClose = rindex($line, "</pre>");
		if ($inPre) {
			$inPre = ($lastPreClose>-1 && $lastPreClose>$lastPreOpen) ? 0 : 1;
		}
		else {
			$inPre = $lastPreOpen > $lastPreClose;
		}
		my $lastListOpen = max(rindex($line, "<ol>"), rindex($line, "<ul>"));
		my $lastListClose = max(rindex($line, "</ol>"), rindex($line, "</ul>"));
		if ($inList) {
			$inList = ($lastListClose>-1 && $lastListClose>$lastListOpen) ? 0 : 1;
		}
		else {
			$inList = $lastListOpen > $lastListClose;
		}
		$curAttrDesc .= $line . (($inPre || $inList || ($line =~ /<\/h5>$/)) ? "\n" : "<br>\n");
		$curAttrDesc =~ s!</?p>!!g; # Strip off paragraph elements; we rely on newlines

	}

}


# Generate XML from our newfound knowledge.
foreach my $key (sort(keys(%tagToDesc))) {

	print OUT "\t<keyword name=\"$key\" type=\"tag\">\n";
	print OUT "\t\t<desc><![CDATA[$tagToDesc{$key}]]></desc>\n";

	if (defined($tagsToAttributes{$key})) {
		my @attrs = @{$tagsToAttributes{$key}};
		if (@attrs) {
			print OUT "\t\t<params>\n";
			foreach my $attr (sort(@attrs)) {
print "...... $attr: " . $attrsToType{$attr} . "\n";

				print OUT "\t\t\t<param name=\"$attr\" type=\"$attrsToType{$attr}\">\n";
				if (defined($attrsToDesc{$attr})) {
					print OUT "\t\t\t\t<desc><![CDATA[$attrsToDesc{$attr}]]></desc>\n";
				}
				print OUT "\t\t\t</param>\n";
			}
			print OUT "\t\t</params>\n";
		}
	}
	print OUT "\t</keyword>\n";

}

close(INFILE);


# Print footer of XML definition file
print OUT <<EOT;
</keywords>

</api>
EOT
