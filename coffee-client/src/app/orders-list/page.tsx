// src/app/orders-list/page.tsx
"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

type Order = {
    id: string;
    customerName: string;
    coffeeType: string;
    milkType: string;
    numShots: number;
    syrups: string[];
    status: string;
    createdAt: string;
};

export default function OrdersListPage() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loadingIds, setLoadingIds] = useState<Set<string>>(new Set());
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();

    // fetch pending orders
    const load = async () => {
        try {
            const res = await fetch("http://localhost:8081/orders", {
                cache: "no-store",
            });
            if (!res.ok) throw new Error("Failed to fetch pending orders");
            setOrders(await res.json());
        } catch (err: any) {
            setError(err.message);
        }
    };

    useEffect(() => {
        load();
    }, []);

    // format createdAt as "Xm Ys ago"
    function formatTimeAgo(iso: string) {
        const now = Date.now();
        const created = new Date(parseFloat(iso) * 1000).getTime();
        const diffSecs = Math.floor((now - created) / 1000);
        const m = Math.floor(diffSecs / 60);
        const s = diffSecs % 60;
        return m > 0 ? `${m}m ${s}s ago` : `${s}s ago`;
    }

    // handle ready / cancel
    const handle = async (id: string, action: "complete" | "cancel") => {
        setError(null);
        setLoadingIds((ids) => new Set(ids).add(id));
        try {
            const res = await fetch(
                `http://localhost:8081/orders/orders/${id}/${action}`,
                { method: "POST" }
            );
            if (!res.ok) throw new Error(`${action} failed`);
            // remove from list
            setOrders((o) => o.filter((x) => x.id !== id));
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoadingIds((ids) => {
                const next = new Set(ids);
                next.delete(id);
                return next;
            });
        }
    };

    return (
        <main className="p-8 max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold mb-6">📝 Pending Orders</h1>
            {error && (
                <div className="text-red-600 mb-4">
                    ❌ <strong>Error:</strong> {error}
                </div>
            )}
            {orders.length === 0 ? (
                <p className="text-gray-600">No orders are currently pending.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white border border-gray-200">
                        <thead className="bg-gray-50">
                        <tr>
                            {[
                                "Order ID",
                                "Customer",
                                "Coffee",
                                "Milk",
                                "Shots",
                                "Syrups",
                                "Age",
                                "Actions",
                            ].map((col) => (
                                <th
                                    key={col}
                                    className="px-4 py-2 text-left text-sm font-medium text-gray-700 border-b"
                                >
                                    {col}
                                </th>
                            ))}
                        </tr>
                        </thead>
                        <tbody>
                        {orders.map((o) => (
                            <tr key={o.id} className="hover:bg-gray-50">
                                <td className="px-4 py-2 text-sm text-gray-800 border-b">
                                    {o.id}
                                </td>
                                <td className="px-4 py-2 border-b">{o.customerName}</td>
                                <td className="px-4 py-2 border-b">{o.coffeeType}</td>
                                <td className="px-4 py-2 border-b">{o.milkType}</td>
                                <td className="px-4 py-2 border-b">{o.numShots}</td>
                                <td className="px-4 py-2 border-b">
                                    {o.syrups.join(", ")}
                                </td>
                                <td className="px-4 py-2 text-sm text-gray-600 border-b">
                                    {formatTimeAgo(o.createdAt)}
                                </td>
                                <td className="px-4 py-2 border-b space-x-2">
                                    <button
                                        onClick={() => handle(o.id, "complete")}
                                        disabled={loadingIds.has(o.id)}
                                        className="px-3 py-1 rounded bg-green-500 text-white hover:bg-green-600 disabled:opacity-50"
                                    >
                                        {loadingIds.has(o.id) ? "…" : "Ready"}
                                    </button>
                                    <button
                                        onClick={() => handle(o.id, "cancel")}
                                        disabled={loadingIds.has(o.id)}
                                        className="px-3 py-1 rounded bg-red-500 text-white hover:bg-red-600 disabled:opacity-50"
                                    >
                                        {loadingIds.has(o.id) ? "…" : "Cancel"}
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </main>
    );
}
