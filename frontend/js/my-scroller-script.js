const myScroller = document.getElementById('my-scroller');
function scrollLeft() {
  console.log("Scroll left!");
  const firstChild = myScroller.firstElementChild;
  const lastChild = myScroller.lastElementChild;
  const leftMostChild = myScroller.querySelector(':nth-child(2)');
  if (leftMostChild !== firstChild) {
    leftMostChild.previousElementSibling.scrollIntoView({ behavior: 'smooth' });
  } else {
    lastChild.scrollIntoView({ behavior: 'smooth' });
  }
}
function scrollRight() {
  console.log("Scroll right!");
  const firstChild = myScroller.firstElementChild;
  const lastChild = myScroller.lastElementChild;
  const rightMostChild = myScroller.querySelector(':nth-last-child(2)');
  if (rightMostChild !== lastChild) {
    rightMostChild.nextElementSibling.scrollIntoView({ behavior: 'smooth' });
  } else {
    firstChild.scrollIntoView({ behavior: 'smooth' });
  }
}
