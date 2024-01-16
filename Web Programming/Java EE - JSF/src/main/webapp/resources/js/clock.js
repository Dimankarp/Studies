let clockIsActive = true;
const DATE_FORMAT_OPTIONS = {
    hour12: false,
    localeMatcher: "lookup",
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "none",
    minute: "2-digit",
    second: "2-digit"
};

const TIME_FORMAT_OPTIONS = {
    hour12: false,
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
};


document.addEventListener("DOMContentLoaded", function () {
    const dateSpan = document.getElementById('clock-date')
    const timeSpan = document.getElementById('clock-time')

    function updateClock() {
        console.log("Updated")
        let now = new Date()
        dateSpan.innerHTML = now.toLocaleDateString()
        timeSpan.innerHTML = now.toLocaleTimeString(['en-US', 'ru-RU'], TIME_FORMAT_OPTIONS)
    }
    function resetTimeout(){
        window.setTimeout(()=>{
            updateClock()
            if(clockIsActive){
                resetTimeout()
            }

        }, 12000)
    }
    updateClock()
    resetTimeout()
})


