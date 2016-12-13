_hac()
{
    local cur prev opts
    COMPREPLY=()
    cur="${COMP_WORDS[COMP_CWORD]}"
    prev="${COMP_WORDS[COMP_CWORD-1]}"
#    opts=`grep '^ *"[a-zA-Z0-9]\+": *{' ~/.hac/config.json | sed -e 's/.*"\(.*\)".*/\1/' | tr '\n' ' '}`
    opts=`jq -cr 'keys' ~/.hac/config.json | sed -e 's/\(\["\|","\|"\]\)/ /g'`
    if [[ "$prev" != "hac" ]] ; then
        _filedir
    else
        COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
        return 0
    fi
}
complete -F _hac hac
