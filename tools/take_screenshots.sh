#set -x

function yes_NO()
{
   # $1 prompt
   read -n1 -p "${1} (y/N)?" response
   [[ "${response}" == [yY] ]] && return 1 || return 0
}

function YES_no()
{
   # $1 prompt
   read -n1 -p "${1} (Y/n)?" response
   [[ "${response}" != [nN] ]] && return 1 || return 0
}

function take_sceenshot()
{
  # $1 options
  # $2 prompt
  # $3 file out
  # $4 width
  # $5 height
 
   read -n1 -p "In ${1} mode (Y/n)?" response
   echo ""
   [[ $response != [nN] ]] && {
      screenshot2 $5 "raw_${2}.png"
      inkscape "raw_${2}.png" --export-png="${2}.png" --export-area=0:0:$3:$4
   }
}


mkdir -p ../screenshots/
pushd ../screenshots/

yes_NO "Take screenshots for Xoom"
if [ $? == 1 ]; then
   YES_no "Landscape"
   if [ $? == 1 ]; then
      screenshot2 raw_xoom_land.png
      inkscape raw_xoom_land.png --export-png=xoom_land.png --export-area=0:80:1280:800
   fi
   
   YES_no "Portrait"
   if [ $? == 1 ]; then
      screenshot2 -l raw_xoom_port.png
      inkscape raw_xoom_port.png --export-png=xoom_port.png --export-area=0:80:800:1280
   fi
fi

YES_no "Take screenshots for Bionic"
if [ $? == 1 ]; then
   YES_no "Portrait"
   if [ $? == 1 ]; then
      screenshot2 raw_bionic_port.png
      inkscape raw_bionic_port.png --export-png=bionic_port.png --export-area=0:0:544:920
   fi

   YES_no "Landscape"
   if [ $? == 1 ]; then
      screenshot2 -l raw_bionic_land.png
      inkscape raw_bionic_land.png --export-png=bionic_land.png --export-area=0:0:960:504
   fi
fi

YES_no "Take screenshots for Atrix"
if [ $? == 1 ]; then
   YES_no "Portrait"
   if [ $? == 1 ]; then
      screenshot2 raw_bionic_port.png
      inkscape raw_bionic_port.png --export-png=bionic_port.png --export-area=0:0:540:920
   fi

   YES_no "Landscape"
   if [ $? == 1 ]; then
      screenshot2 -l raw_bionic_land.png
      inkscape raw_bionic_land.png --export-png=bionic_land.png --export-area=0:0:960:500
   fi
fi

popd
