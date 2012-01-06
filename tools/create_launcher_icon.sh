#set -x
#set -e

function export_image()
{
   # $1 input file
   # $2 output dir
   # $3 output file
   # $4 height/width

   mkdir -p $2
   inkscape $1 --export-png="${2}${3}" --export-width=$4 --export-height=$4 --export-background-opacity=0
}

if [ -z $1 ]; then
   exit -1
fi
   
if [ -e $2 ]; then
   out="ic_launcher.png"
else
   out=$2
fi

export_image $1 "../res/drawable-ldpi/" $out "36"
export_image $1 "../res/drawable-mdpi/" $out "48"
export_image $1 "../res/drawable-hdpi/" $out "72"
export_image $1 "../res/drawable-xhdpi/" $out "96"
export_image $1 "../release/" $out "512"
