#!/usr/local/bin/perl
#
# create_html_xml.pl - Generates the XML API file for RSyntaxTextArea.
#
# Usage:
#     perl create_html_xml.pl
#
use strict;
use Cwd qw(abs_path);
use File::Basename;

my $this_script = abs_path($0);
my $dir = dirname($this_script);
my $outfile = "$dir/../html.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");


# Print header of XML definition file
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>

<!--

   html.xml - API specification for HTML 5.
              Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1

   This file was generated from: $this_script
   on date: $datestamp

 -->
<api language="HTML">
<!--
	<environment paramStartChar="(" paramEndChar=")" paramSeparator=", " terminal=";" />
-->

<keywords>
EOT


# Create mapping of tags to descriptions, and tags to supported HTML versions
my %tagToDesc;
my %tagToHTMLVersion;
open(INFILE, "html5_tags_to_descs.txt") || die("Cannot open INFILE: $!\n");
# skip first few lines
my $line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;
while (chomp($line = <INFILE>)) {
	my ($tag, $desc, $in4, $in5) = split(/\t+/, $line);
	$tag =~ s!^\s+!!g;
	$tag =~ s!\s+$!!g;
	$desc =~ s!^\s+!!g;
	$desc =~ s!\s+$!!g;
	$tagToDesc{$tag} = $desc;
	if (($in4 =~ /\s*4\s*/) && ($in5 =~ /\s*5\s*/)) {
		$tagToHTMLVersion{$tag} = "HTML 4, 5";
	}
	elsif ($in5 =~ /\s*5\s*/) {
		$tagToHTMLVersion{$tag} = "HTML 5";
	}
	else {
		$tagToHTMLVersion{$tag} = "HTML 4";
	}
}
close(INFILE);


# create mapping of tags to their attribuets, mapping of attributes to their
# descriptions, etc.
my %tagsToAttributes;
my %attrsToDesc;
my %attrsToType;

open(INFILE, "html_401_attrs.txt") || die("Cannot open INFILE: $!\n");

# Skip first 6 lines
my $line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;

while (chomp($line = <INFILE>)) {
	my ($attr, $elemsStr, $type, undef, undef, undef, $attrDesc) = split(/\t+/, $line);
	$attr =~ s!^\s+!!g;
	$attr =~ s!\s+$!!g;
	$attrDesc =~ s!^\s+!!g;
	$attrDesc =~ s!\s+$!!g;
	my @elems = split(", ", $elemsStr);
	foreach my $elem (@elems) {
		$elem = lc($elem);
		$elem =~ s!^\s+!!g;
		$elem =~ s!\s+$!!g;
		push(@{$tagsToAttributes{$elem}}, $attr);
	}
	if (defined($attrDesc) && length($attrDesc)>0) {
		$attrsToDesc{$attr} = $attrDesc;
	}
	$attrsToType{$attr} = $type;
}


# Generate XML from our newfound knowledge.  Use tagToDesc since it is HTML 5.
foreach my $key (sort(keys(%tagToDesc))) {

	print OUT "\t<keyword name=\"$key\" type=\"tag\" definedIn=\"$tagToHTMLVersion{$key}\">\n";
	print OUT "\t\t<desc>$tagToDesc{$key}</desc>\n";

	if (defined($tagsToAttributes{$key})) {
		my @attrs = @{$tagsToAttributes{$key}};
		if (defined(@attrs)) {
			print OUT "\t\t<params>\n";
			foreach my $attr (sort(@attrs)) {
				print OUT "\t\t\t<param name=\"$attr\" type=\"$attrsToType{$attr}\">\n";
				if (defined($attrsToDesc{$attr})) {
					print OUT "\t\t\t\t<desc>$attrsToDesc{$attr}</desc>\n";
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
