<script lang="ts">
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { api } from "$lib/api";
  import Chart from "chart.js/auto";

  let userName = "";
  let currentPrice = 0;
  let totalToday = 0;
  let chartCanvas: HTMLCanvasElement;

  onMount(async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      goto("/");
      return;
    }

    userName = localStorage.getItem("userName") || "";

    try {
      // Fetch current price
      const price = await api("/api/prices/current");
      currentPrice = price.pricePerKwh;

      // Fetch last 24h consumption
      const readings = await api("/api/consumption?hours=24");

      // Calculate total
      totalToday = readings.reduce((sum: number, r: any) => sum + r.kwh, 0);
      totalToday = Math.round(totalToday * 100) / 100;

      // Build chart
      const labels = readings.map((r: any) =>
        new Date(r.timestamp).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        }),
      );
      const data = readings.map((r: any) => r.kwh);

      new Chart(chartCanvas, {
        type: "bar",
        data: {
          labels,
          datasets: [
            {
              label: "Consumption (kWh)",
              data,
              backgroundColor: "#3b82f6",
              borderRadius: 4,
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: { beginAtZero: true, title: { display: true, text: "kWh" } },
            x: { ticks: { maxTicksAutoSkip: true, maxRotation: 45 } },
          },
        },
      });
    } catch (e) {
      console.error(e);
      goto("/");
    }
  });

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userName");
    goto("/");
  }
</script>

<div class="container">
  <header>
    <h1>⚡ EnergyDashboard</h1>
    <div>
      <span>Welcome, {userName}</span>
      <button on:click={logout}>Logout</button>
    </div>
  </header>

  <div class="cards">
    <div class="card">
      <h3>Current Price</h3>
      <p class="value">
        €{currentPrice.toFixed(3)}<span class="unit">/kWh</span>
      </p>
    </div>
    <div class="card">
      <h3>Last 24h Usage</h3>
      <p class="value">{totalToday}<span class="unit"> kWh</span></p>
    </div>
  </div>

  <div class="chart-container">
    <h3>Consumption (Last 24 Hours)</h3>
    <canvas bind:this={chartCanvas}></canvas>
  </div>
</div>

<style>
  .container {
    max-width: 900px;
    margin: 0 auto;
    padding: 20px;
  }
  header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
  }
  header button {
    margin-left: 16px;
    padding: 6px 14px;
    background: #ef4444;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }
  .cards {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 30px;
  }
  .card {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 20px;
    text-align: center;
  }
  .card h3 {
    margin: 0 0 8px 0;
    color: #64748b;
    font-size: 14px;
  }
  .value {
    font-size: 32px;
    font-weight: bold;
    color: #1e293b;
    margin: 0;
  }
  .unit {
    font-size: 16px;
    color: #94a3b8;
  }
  .chart-container {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 20px;
  }
  .chart-container h3 {
    margin: 0 0 16px 0;
    color: #64748b;
  }
</style>
