'use client'

import { useState, FormEvent } from "react";

type OrderRequest = {
    customerName: string;
    coffeeType: string;
    milkType: string;
    numShots: number;
    syrups: string[];
};

export default function OrderPage() {
    const [form, setForm] = useState<OrderRequest>({
        customerName: "",
        coffeeType: "latte",
        milkType: "dairy",
        numShots: 1,
        syrups: [],
    });
    const [loading, setLoading] = useState(false);
    const [orderId, setOrderId] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const syrupOptions = ["vanilla", "caramel", "hazelnut", "chocolate"];
    const coffeeOptions = ["espresso", "latte", "cappuccino", "americano"];
    const milkOptions = ["dairy", "oat", "almond", "soy"];

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
    ) => {
        const { name, value, type } = e.target;
        const checked = 'checked' in e.target ? e.target.checked : false;

        if (name === "syrups") {
            setForm((f) => {
                const set = new Set(f.syrups);
                if (checked) set.add(value);
                else set.delete(value);
                return { ...f, syrups: Array.from(set) };
            });
        } else if (type === "number") {
            setForm((f) => ({ ...f, [name]: parseInt(value, 10) }));
        } else {
            setForm((f) => ({ ...f, [name]: value }));
        }
    };

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setOrderId(null);

        try {
            const res = await fetch("http://localhost:8080/orders", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(form),
            });
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || res.statusText);
            }
            const data = await res.json();
            setOrderId(data.orderId);
        } catch (err: any) {
            setError(err.message || "Unknown error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-white rounded-xl shadow-md p-6">
                <h1 className="text-2xl font-semibold mb-4 text-center">
                    Place Your Coffee Order ☕
                </h1>

                <form onSubmit={onSubmit} className="space-y-4">
                    {/* Customer Name */}
                    <div>
                        <label className="block text-sm font-medium mb-1">
                            Your Name
                        </label>
                        <input
                            name="customerName"
                            type="text"
                            required
                            value={form.customerName}
                            onChange={handleChange}
                            className="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                        />
                    </div>

                    {/* Coffee Type */}
                    <div>
                        <label className="block text-sm font-medium mb-1">
                            Coffee Type
                        </label>
                        <select
                            name="coffeeType"
                            value={form.coffeeType}
                            onChange={handleChange}
                            className="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                        >
                            {coffeeOptions.map((c) => (
                                <option key={c} value={c}>
                                    {c.charAt(0).toUpperCase() + c.slice(1)}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Milk Type */}
                    <div>
                        <label className="block text-sm font-medium mb-1">
                            Milk Type
                        </label>
                        <select
                            name="milkType"
                            value={form.milkType}
                            onChange={handleChange}
                            className="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                        >
                            {milkOptions.map((m) => (
                                <option key={m} value={m}>
                                    {m.charAt(0).toUpperCase() + m.slice(1)}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Number of Shots */}
                    <div>
                        <label className="block text-sm font-medium mb-1">
                            Number of Espresso Shots
                        </label>
                        <input
                            name="numShots"
                            type="number"
                            min={1}
                            max={5}
                            value={form.numShots}
                            onChange={handleChange}
                            className="w-24 border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                        />
                    </div>

                    {/* Syrups */}
                    <div>
                        <label className="block text-sm font-medium mb-1">
                            Extra Syrups
                        </label>
                        <div className="flex flex-wrap gap-2">
                            {syrupOptions.map((s) => (
                                <label
                                    key={s}
                                    className="inline-flex items-center space-x-1"
                                >
                                    <input
                                        type="checkbox"
                                        name="syrups"
                                        value={s}
                                        checked={form.syrups.includes(s)}
                                        onChange={handleChange}
                                        className="rounded text-indigo-600 focus:ring-indigo-500"
                                    />
                                    <span className="text-sm">
                    {s.charAt(0).toUpperCase() + s.slice(1)}
                  </span>
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Submit */}
                    <div className="pt-4">
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-indigo-600 text-white py-2 rounded hover:bg-indigo-700 disabled:opacity-50"
                        >
                            {loading ? "Placing Order…" : "Place Order"}
                        </button>
                    </div>
                </form>

                {/* Feedback */}
                {orderId && (
                    <p className="mt-4 text-green-600 text-center">
                        ✅ Order placed! Your order ID is{" "}
                        <code className="font-mono">{orderId}</code>
                    </p>
                )}
                {error && (
                    <p className="mt-4 text-red-600 text-center">
                        ❌ {error}
                    </p>
                )}
            </div>
        </div>
    );
}