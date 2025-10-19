import { Document, Page, pdfjs } from "react-pdf";
import { useRef, useState, useEffect } from "react";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";

interface ScrollablePdfViewerProps {
	fileUrl: string;
	onScrolledToEnd?: () => void;
	maxHeight?: number; // instead of fixed height
	minHeight?: number;
	className?: string;
}

pdfjs.GlobalWorkerOptions.workerSrc = `https://unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

const ScrollablePdfViewer: React.FC<ScrollablePdfViewerProps> = ({
	fileUrl,
	onScrolledToEnd,
	maxHeight = 540,
	minHeight = 300,
	className = "",
}) => {
	const containerRef = useRef<HTMLDivElement>(null);
	const [numPages, setNumPages] = useState<number>(0);
	const [containerWidth, setContainerWidth] = useState<number>(0);

	const handleScroll = () => {
		const container = containerRef.current;
		if (!container) return;

		const { scrollTop, scrollHeight, clientHeight } = container;
		if (scrollTop + clientHeight >= scrollHeight - 5) {
			onScrolledToEnd?.();
		}
	};

	// 🔹 Automatically update page width when the container resizes
	useEffect(() => {
		const updateWidth = () => {
			if (containerRef.current) {
				setContainerWidth(containerRef.current.offsetWidth);
			}
		};

		updateWidth();
		window.addEventListener("resize", updateWidth);
		return () => window.removeEventListener("resize", updateWidth);
	}, []);

	return (
		<div
			ref={containerRef}
			onScroll={handleScroll}
			className={`overflow-y-auto overflow-x-hidden border border-surface-400/20 rounded-xl bg-surface-800/30 shadow-inner ${className}`}
			style={{
				maxHeight: `${maxHeight}px`,
				minHeight: `${minHeight}px`,
				boxSizing: "border-box",
			}}
		>
			<Document
				file={fileUrl}
				onLoadSuccess={({ numPages }) => setNumPages(numPages)}
				loading={<p className="text-center text-sm py-4">Loading PDF...</p>}
			>
				{Array.from({ length: numPages }, (_, index) => (
					<div key={index} className="flex justify-center my-1">
						<Page
							pageNumber={index + 1}
							width={Math.min(containerWidth * 0.95, 600)} // 🔹 responsive max width
							renderTextLayer
							renderAnnotationLayer
						/>
					</div>
				))}
			</Document>
		</div>
	);
};

export default ScrollablePdfViewer;
