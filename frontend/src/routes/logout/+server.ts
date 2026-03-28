import { redirect } from '@sveltejs/kit';
import type {RequestHandler} from "@sveltejs/kit";
import {PUBLIC_BACKEND_2} from "$env/static/public";

export const POST: RequestHandler = async ({ cookies }) => {
    cookies.delete('refreshToken', { path: '/api/auth/refresh' });
    // await fetch(`${PUBLIC_BACKEND_2}/auth/logout`)
    return new Response(null, {status: 204})
};