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


mkdir -p ../screenshots/
pushd ../screenshots/

yes_NO "Take screenshots for Xoom"
if [ $? == 1 ]; then
   YES_no "Landscape"
   if [ $? == 1 ]; then
      screenshot2 /tmp/raw_xoom_land.png
      inkscape /tmp/raw_xoom_land.png --export-png=/tmp/xoom_land.png --export-area=0:80:1280:800
      pngcrush /tmp/xoom_land.png xoom_land.png
   fi

   YES_no "Portrait"
   if [ $? == 1 ]; then
      screenshot2 -l /tmp/raw_xoom_port.png
      inkscape /tmp/raw_xoom_port.png --export-png=/tmp/xoom_port.png --export-area=0:80:800:1280
      pngcrush /tmp/xoom_land.png xoom_port.png
   fi
fi

YES_no "Take screenshots for Bionic"
if [ $? == 1 ]; then
   YES_no "Portrait"
   if [ $? == 1 ]; then
      screenshot2 /tmp/raw_bionic_port.png
      inkscape /tmp/raw_bionic_port.png --export-png=/tmp/bionic_port.png --export-area=0:0:544:920
      pngcrush /tmp/bionic_port.png bionic_port.png
   fi

   YES_no "Landscape"
   if [ $? == 1 ]; then
      screenshot2 -l /tmp/raw_bionic_land.png
      inkscape /tmp/raw_bionic_land.png --export-png=/tmp/bionic_land.png --export-area=0:0:960:504
      pngcrush /tmp/bionic_land.png bionic_land.png
   fi
fi

popd
