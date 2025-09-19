/// <reference types="vite/client" />

interface ImportMetaEnv {
	readonly VITE_BACKEND_API_URL: string;
	readonly VITE_BACKEND_SOCKET_URL: string;
	// readonly VITE_OTHER_KEY?: string;
	readonly [key: string]: string | undefined;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}
