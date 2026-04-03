// 날짜 변경 함수
export const changeDate = (val) => {
  let date = new Date(val);
  const today = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;

  return today;
};
