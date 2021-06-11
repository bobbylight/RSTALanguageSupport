#!/usr/local/bin/perl
#
# create_asm6502_xml.pl - Generates the 6502 Assembler API file for RSyntaxTextArea.
#
# Usage:
#     perl create_asm6502_xml.pl
#
use strict;
use Cwd qw(abs_path);
use File::Basename;

my $this_script = abs_path($0);
my $dir = dirname($this_script);
my $outfile = "$dir/../asm6502.xml";

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $datestamp = sprintf("%4d-%02d-%02d %02d:%02d:%02d\n",
					$year+1900,$mon+1,$mday,$hour,$min,$sec);

open(OUT, ">$outfile") || die("Cannot open outfile: $!\n");


# Print header of XML definition file
print OUT <<EOT;
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<!DOCTYPE api SYSTEM \"CompletionXml.dtd\">

<!--

   asm6502.xml - API specification for 6502 Assembler.
                 Used by RSyntaxTextArea to provide code completion.

   Author:         Robert Futrell
   Version:        0.1

   This file was generated from: $this_script
   on date: $datestamp

 -->
<api language="asm6502">

	<environment paramStartChar="" paramEndChar="" paramSeparator=", " terminal="" />

<keywords>
EOT


# Create mapping of tags to descriptions, and tags to supported HTML versions
my %instructionToDesc;
open(INFILE, "asm-6502-instructions.txt") || die("Cannot open INFILE: $!\n");
# skip first few lines
my $line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;
$line = <INFILE>;

while (chomp($line = <INFILE>)) {

    my ($instruction, $description) = split(/\t/, $line, 2);
    $instructionToDesc{$instruction} = $description;
}
close(INFILE);


# Generate XML from our newfound knowledge.
foreach my $key (sort(keys(%instructionToDesc))) {

	print OUT "\t<keyword name=\"$key\" type=\"function\">\n";
	print OUT "\t\t<desc>$instructionToDesc{$key}</desc>\n";

#	if (defined($tagsToAttributes{$key})) {
#		my @attrs = @{$tagsToAttributes{$key}};
#		if (@attrs) {
#			print OUT "\t\t<params>\n";
#			foreach my $attr (sort(@attrs)) {
#				print OUT "\t\t\t<param name=\"$attr\" type=\"$attrsToType{$attr}\">\n";
#				if (defined($attrsToDesc{$attr})) {
#					print OUT "\t\t\t\t<desc>$attrsToDesc{$attr}</desc>\n";
#				}
#				print OUT "\t\t\t</param>\n";
#			}
#			print OUT "\t\t</params>\n";
#		}
#	}
	print OUT "\t</keyword>\n";

}

close(INFILE);


# Print footer of XML definition file
print OUT <<EOT;
</keywords>

</api>
EOT
