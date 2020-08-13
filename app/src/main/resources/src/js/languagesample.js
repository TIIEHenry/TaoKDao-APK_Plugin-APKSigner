var defaultLanguageCountry="default"
var strings={
"sample":{
    defaultLanguageCountry:"示例",
    "zh-CN":"示例",
    "en":"Sample",
    }
}
var lc=activity.getLanguageCountry()
function lan(map){
    var s=map[lc]
    if(s!=null)
        return s
    s=map[defaultLanguageCountry]
    if(s!=null)
        return s
    return map.values.first()
}
function processLanguage(map){
    map.forEach((k,v)=>
    strings[k]=lan(v)
)
}
print(strings["sample"])