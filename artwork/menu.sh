set -x
set -e

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
   bname=`basename $1 .svg`
   out="ic_menu_${bname}.png"
else
   out=$2
fi
echo $out

export_image $1 "./out/res/drawable-ldpi/" $out "24"
export_image $1 "./out/res/drawable-mdpi/" $out "32"
export_image $1 "./out/res/drawable-hdpi/" $out "48"
export_image $1 "./out/res/drawable-xhdpi/" $out "96"
