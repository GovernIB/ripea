import { useRef, useCallback } from 'react';
import { useResourceApiService } from 'reactlib';

type PollingOptions = {
	intervalMs?: number;
	//timeoutMs?: number;
	stopCondition?: (data: any) => boolean;
	onProgress?: (data: any) => void;
};

type UsePollingArtifactActionReturn = {
	startPolling: (id: any, code: string, args?: any) => Promise<any>;
	cancelPolling: (id: any, code: string, args?: any) => void;
};

export const usePollingArtifactAction = (
	resourceName: string,
	options?: PollingOptions
): UsePollingArtifactActionReturn => {
	const api = useResourceApiService(resourceName);
	const isCancelledRef = useRef(false);

	const startPolling = useCallback(
		async (id: any, code: string, args?: any) => {
			isCancelledRef.current = false;

			const {
				intervalMs = 2000,
				//timeoutMs = 60000,
				stopCondition = (data: any) => data?.finished,
			} = options ?? {};

			while (true) {

				if (isCancelledRef.current) {
					return null;
				}

				const result = await api.artifactAction(id, { code, ...args });

				options?.onProgress?.(result);

				if (stopCondition(result)) {
					return result;
				}

				await new Promise((r) => setTimeout(r, intervalMs));
			}
		},
		[api, options]
	);

	const cancelPolling = useCallback(
		async (id: any, code: string, args?: any) => {
			isCancelledRef.current = true;
			try {
				await api.artifactAction(id, { code, ...args });
			} catch (error) {
				console.error("Error cancelant polling en el backend:", error);
			}
		}, [api]);


	return { startPolling, cancelPolling };
};
