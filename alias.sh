alias log="adb logcat | grep -i trainscorekeeper"
alias clog="adb logcat -c;adb logcat | grep -i trainscorekeeper"
alias dlog="adb logcat -d | grep -i trainscorekeeper"
alias logcept='adb logcat -d -v threadtime | egrep -i "(system.err|androidruntime)"'

alias runmain='adb shell am start -a android.intent.action.MAIN mikecorrigan.trainscorekeeper/com.mikecorrigan.trainscorekeeper.MainActivity'
alias runnew='adb shell am start -a android.intent.action.MAIN mikecorrigan.trainscorekeeper/com.mikecorrigan.trainscorekeeper.ActivityNewGame'
alias prefs='adb shell am start -a android.intent.action.MAIN mikecorrigan.trainscorekeeper/com.mikecorrigan.trainscorekeeper.Preferences'

alias getsaved='adb pull /data/data/mikecorrigan.trainscorekeeper/files/saved0.txt'
alias putsaved='adb push saved0.txt /data/data/mikecorrigan.trainscorekeeper/files/saved0.txt'

alias dockoff="adb shell am broadcast -a android.intent.action.DOCK_EVENT --ei android.intent.extra.DOCK_STATE 0"
alias dockdesk="adb shell am broadcast -a android.intent.action.DOCK_EVENT --ei android.intent.extra.DOCK_STATE 1"
alias dockcar="adb shell am broadcast -a android.intent.action.DOCK_EVENT --ei android.intent.extra.DOCK_STATE 2"
alias dockledesk="adb shell am broadcast -a android.intent.action.DOCK_EVENT --ei android.intent.extra.DOCK_STATE 3"
alias dockhedesk="adb shell am broadcast -a android.intent.action.DOCK_EVENT --ei android.intent.extra.DOCK_STATE 4"

alias densityreset="adb shell wm density reset"
alias density160="adb shell wm density 160"
alias density240="adb shell wm density 240"
alias density320="adb shell wm density 320"
alias density480="adb shell wm density 480"

alias sizereset="adb shell wm size reset"
alias sizesm="adb shell wm size 600x400"

copyright() {
  word="Copyright"
  path="src"
  type="f"
  files="*.java"
  find="find ${path} -type ${type} -name ${files}"
  #echo ${find}

  echo ${word}
  echo -n "Total:";${find} | wc -l
  echo -n "With: ";${find} | xargs grep ${word} | wc -l
  echo -n "Without: ";${find} |  xargs grep -H -c ${word} | grep 0$ | wc -l
  ${find} | xargs grep -H -c ${word} | grep 0$ | cut -d':' -f1
}
