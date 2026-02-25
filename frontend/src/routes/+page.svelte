<script lang="ts">
    import { goto } from '$app/navigation';
    import { api } from '$lib/api';

    let email = '';
    let password = '';
    let name = '';
    let isRegister = false;
    let error = '';

    async function handleSubmit() {
        error = '';
        try {
            const endpoint = isRegister ? '/auth/register' : '/auth/login';
            const body = isRegister
                ? { email, password, name }
                : { email, password };

            const data = await api(endpoint, {
                method: 'POST',
                body: JSON.stringify(body)
            });

            localStorage.setItem('token', data.token);
            localStorage.setItem('userName', data.name);
            goto('/dashboard');
        } catch (e: any) {
            error = e.message || 'Something went wrong';
        }
    }
</script>

<div class="container">
    <h1>⚡ EnergyDashboard</h1>
    <form on:submit|preventDefault={handleSubmit}>
        {#if isRegister}
            <input type="text" bind:value={name} placeholder="Name" required />
        {/if}
        <input type="email" bind:value={email} placeholder="Email" required />
        <input type="password" bind:value={password} placeholder="Password" required />

        {#if error}
            <p class="error">{error}</p>
        {/if}

        <button type="submit">{isRegister ? 'Register' : 'Login'}</button>
    </form>
    <p>
        <button class="link" on:click={() => (isRegister = !isRegister)}>
            {isRegister ? 'Already have an account? Login' : "Don't have an account? Register"}
        </button>
    </p>
</div>

<style>
    .container {
        max-width: 400px;
        margin: 100px auto;
        text-align: center;
    }
    form {
        display: flex;
        flex-direction: column;
        gap: 12px;
    }
    input {
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 6px;
        font-size: 16px;
    }
    button[type="submit"] {
        padding: 10px;
        background: #2563eb;
        color: white;
        border: none;
        border-radius: 6px;
        font-size: 16px;
        cursor: pointer;
    }
    button[type="submit"]:hover {
        background: #1d4ed8;
    }
    .link {
        background: none;
        border: none;
        color: #2563eb;
        cursor: pointer;
        text-decoration: underline;
    }
    .error {
        color: red;
        margin: 0;
    }
</style>