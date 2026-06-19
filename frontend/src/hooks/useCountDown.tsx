import { useCallback, useEffect, useRef, useState } from "react";

const useCountDown = (initialMillis: number, interval: number = 1000) => {
    const [remainingTime, setRemainingTime] = useState<number>(initialMillis);
    const intervalIdRef = useRef<ReturnType<typeof setInterval> | null>(null);

    const start = useCallback((millis: number) => {
        setRemainingTime(millis);
        intervalIdRef.current = setInterval(() => {
            setRemainingTime((prev) => {
                if (prev <= interval) {
                    clearInterval(intervalIdRef.current!);
                    return 0;
                }
                return prev - interval;
            });
        }, interval);
    }, [interval]);

    useEffect(() => {
        if (initialMillis > 0) {
            start(initialMillis);
        }
        return () => {
            clearInterval(intervalIdRef.current);
        };
    }, [initialMillis, start]);

    return {
        seconds: Math.floor(remainingTime / 1000),
        isFinished: remainingTime <= 0,
        start,
        stop,
    };
};

export default useCountDown;