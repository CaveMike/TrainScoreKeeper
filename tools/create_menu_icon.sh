#set -x
#set -e

function export_image()
{
   # $1 input file
   # $2 output dir
   # $3 output file
   # $4 height/width

   mkdir -p $2
   inkscape $1 --export-png="/tmp/${3}" --export-width=$4 --export-height=$4 --export-background-opacity=0
   pngcrush "/tmp/${3}" "${2}${3}"
}

if [ -z $1 ]; then
   exit -1
fi

if [ -e $2 ]; then
   bname=`basename $1 .svg`
   out="${bname}.png"
else
   out=$2
fi

export_image $1 "../res/drawable-ldpi/" $out "24"
export_image $1 "../res/drawable-mdpi/" $out "32"
export_image $1 "../res/drawable-hdpi/" $out "48"
export_image $1 "../res/drawable-xhdpi/" $out "96"
