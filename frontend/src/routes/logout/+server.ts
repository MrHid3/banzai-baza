import { redirect } from '@sveltejs/kit';
import type {RequestHandler} from "@sveltejs/kit";

export const POST: RequestHandler = async ({ cookies }) => {
    cookies.delete('refreshToken', { path: '/api/auth/refresh' });
    return new Response(null, {status: 204})
};