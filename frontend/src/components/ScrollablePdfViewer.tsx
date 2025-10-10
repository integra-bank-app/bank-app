import { Document, Page, pdfjs } from "react-pdf";
import { useRef, useState } from "react";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";

interface ScrollablePdfViewerProps {
	fileUrl: string;
	onScrolledToEnd?: () => void;
	height?: number;
	width?: number;
	scale?: number;
}

pdfjs.GlobalWorkerOptions.workerSrc = `https://unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

const ScrollablePdfViewer: React.FC<ScrollablePdfViewerProps> = ({
	fileUrl,
	onScrolledToEnd,
	height = 540,
	scale = 2.3,
	width = 560,
}) => {
	const containerRef = useRef<HTMLDivElement>(null);
	const [numPages, setNumPages] = useState<number>(0);

	const handleScroll = () => {
		const container = containerRef.current;
		if (!container) return;

		const { scrollTop, scrollHeight, clientHeight } = container;
		if (scrollTop + clientHeight >= scrollHeight - 5) {
			onScrolledToEnd?.();
		}
	};

	return (
		<div
			ref={containerRef}
			onScroll={handleScroll}
			style={{
				height: `${height}px`,
				width: `${width}px`,
				overflowY: "auto",
				overflowX: "hidden", // ✅ hide horizontal scroll
				boxSizing: "border-box",
			}}
		>
			<Document
				file={fileUrl}
				onLoadSuccess={({ numPages }) => setNumPages(numPages)}
				loading={<p>Loading PDF...</p>}
			>
				{Array.from({ length: numPages }, (_, index) => (
					<div
						key={index}
						style={{
							display: "flex",
							justifyContent: "center",
							marginBottom: "4px",
							overflow: "hidden", // ✅ ensure inner overflow hidden too
						}}
					>
						<Page
							pageNumber={index + 1}
							scale={scale}
							width={300} // ✅ fix width so it doesn’t exceed container
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
