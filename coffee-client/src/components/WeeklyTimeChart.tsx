'use client';

import React, { useState, useEffect } from 'react';
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
} from 'recharts';

type WeeklyAverage = {
    weekStart: number;    // epoch millis
    averageMs: number;    // avg serve time in ms
};

type ChartPoint = {
    week: string;         // “May 12” etc
    averageMin: number;   // decimal minutes, e.g. 4.82
};

export default function WeeklyTimeChart() {
    const [data, setData] = useState<ChartPoint[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch('http://localhost:8081/search/time-to-serve')
            .then((res) => {
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                return res.json() as Promise<WeeklyAverage[]>;
            })
            .then((json) => {
                const pts = json.map((w) => ({
                    week: new Date(w.weekStart).toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                    }),
                    averageMin: w.averageMs / 60000,
                }));
                setData(pts);
            })
            .catch((err) => {
                console.error(err);
                setError(err.message);
            });
    }, []);

    if (error) {
        return <div className="text-red-600">Failed to load chart: {error}</div>;
    } else if (!data.length) {
        return <div>Loading chart…</div>;
    }

    // helper to turn decimal minutes into “M:SS”
    const fmt = (m: number) => {
        const totalSec = Math.round(m * 60);
        const mm       = Math.floor(totalSec / 60);
        const ss       = totalSec % 60;
        return `${mm}:${ss.toString().padStart(2, '0')}`;
    };

    return (
        <ResponsiveContainer width="100%" height={400}>
            <LineChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="week" />
                <YAxis
                    tickFormatter={fmt}
                    label={{ value: 'Serve Time (M:SS)', angle: -90, position: 'insideLeft' }}
                />
                <Tooltip
                    labelFormatter={(week) => `Week of ${week}`}
                    formatter={(value: number) => [fmt(value), 'Avg Serve Time']}
                />
                <Legend />
                <Line
                    type="monotone"
                    dataKey="averageMin"
                    name="Avg Serve Time"
                    dot={false}
                />
            </LineChart>
        </ResponsiveContainer>
    );
}