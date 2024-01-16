const DATE_FORMAT_OPTIONS = {
    hour12: false,
    localeMatcher: "lookup",
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
};

export function getDateFromTimestamp(timestamp){
    return new Date(timestamp).toLocaleString(['en-US', 'ru-RU'], DATE_FORMAT_OPTIONS);
}
