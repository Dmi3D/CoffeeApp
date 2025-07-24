// src/app/orders-list/page.tsx
import React from "react";

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

async function fetchPending(): Promise<Order[]> {
    const res = await fetch("http://localhost:8081/orders", {
        cache: "no-store",
    });
    if (!res.ok) throw new Error("Failed to fetch pending orders");
    return res.json();
}

function formatTimeAgo(timestamp: string): string {
    const now = new Date();
    const created = new Date(parseFloat(timestamp) * 1000);
    
    const diffMs = now.getTime() - created.getTime();
    
    const totalSeconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    
    if (minutes > 0) {
        return `${minutes}m ${seconds}s ago`;
    } else {
        return `${seconds}s ago`;
    }
}

export default async function OrdersListPage() {
    const orders = await fetchPending();

    return (
        <main className="p-8 max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold mb-6">📝 Pending Orders</h1>

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
                                "Created At",
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
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </main>
    );
}