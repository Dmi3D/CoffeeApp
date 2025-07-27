import WeeklyTimeChart from "../../components/WeeklyTimeChart";

export default function AnalyticsPage() {
    return (
        <main className="p-8 max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold mb-6">☕ Analytics</h1>

            <section className="mb-12">
                <h2 className="text-2xl mb-4">Weekly Time-to-Serve</h2>
                <WeeklyTimeChart />
            </section>
        </main>
    );
}