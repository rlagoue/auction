import moment, {MomentInput} from "moment";

export function useDateTimeUtils() {
    const utcDateTimeToLocalString = (value: MomentInput, pattern = "DD.MM.YYYY HH:mm"): string => {
        const localValue = utcDateTimeToLocal(value);
        if (!localValue) {
            return "-";
        }
        return localValue.format(pattern);
    };

    const utcDateTimeToLocal = (value: MomentInput): moment.Moment | null => {
        if (!value) {
            return null;
        }
        return moment.utc(value).local();
    };

    const formatDate = (value: MomentInput, pattern = "DD.MM.YYYY HH:mm"): string => {
        if (!value) {
            return "-";
        }
        return moment(value).format(pattern);
    }

    const localDateToUtc = (value: MomentInput, pattern = "YYYY-MM-DDTHH:mm:ss"): string => {
        if (!value) {
            return "";
        }
        return moment(value, pattern)
            .utc()
            .format(pattern);
    };


    return {
        utcDateTimeToLocalString,
        formatDate,
        localDateToUtc,
    }

}
