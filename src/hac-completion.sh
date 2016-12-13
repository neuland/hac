_hac()
{
    local cur prev opts
    COMPREPLY=()
    cur="${COMP_WORDS[COMP_CWORD]}"
    prev="${COMP_WORDS[COMP_CWORD-1]}"
    opts=`jq -cr 'keys' ~/.hac/config.json | perl -p -e 's/(\["|","|"\])/ /g'`
    if [[ "$prev" != "hac" ]] ; then
        _filedir
    else
        COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
        return 0
    fi
}
complete -F _hac hac
