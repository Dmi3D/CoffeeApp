// src/app/page.tsx
import Link from "next/link";

export default function HomePage() {
    return (
        <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-8">
            <h1 className="text-4xl font-extrabold mb-6">Welcome to Bready Café ☕️</h1>
            <p className="text-lg text-gray-700 mb-10 text-center max-w-xl">
                We’re <span className="font-semibold">bready</span> to serve you.
                Are you here to sip or to serve?
            </p>

            <div className="flex flex-col sm:flex-row gap-6">
                <Link
                    href="/orders"
                    className="px-8 py-4 bg-indigo-600 text-white rounded-lg shadow hover:bg-indigo-700 transition"
                >
                    🥐 I’m a “Toast” Customer
                </Link>
                <Link
                    href="/orders-list"
                    className="px-8 py-4 bg-green-600 text-white rounded-lg shadow hover:bg-green-700 transition"
                >
                    ☕️ I’m a “Toast” Barista
                </Link>
            </div>
        </div>
    );
}
