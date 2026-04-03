// 날짜 변경 함수
export const changeDate = (date) => {
  const today = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;

  return today;
};
