export default interface IPaginationProps {
    currentPage: number;
    totalPages: number;
    onPageChange: (newPage: number) => void;
}