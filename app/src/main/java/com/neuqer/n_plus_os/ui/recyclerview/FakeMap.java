package com.neuqer.n_plus_os.ui.recyclerview;

public class FakeMap {
        private String fileName;
        private int address;
        private char[] currentContent;
        private int currentPage;
        private int totalPage;
        private boolean isOpen;
        public FakeMap(String fileName, int address) {
            this.fileName = fileName;
            this.address = address;
            currentPage = 1;
            totalPage = 1;
            isOpen = false;
            currentContent = new char[]{' ', ' ', ' ', ' '};
        }

        public char[] getCurrentContent() {
            return currentContent;
        }

        public void setCurrentContent(char[] currentContent) {
            this.currentContent = currentContent;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setAddress(int address) {
            this.address = address;
        }



        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getAddress() {
            return address;
        }



        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }
}
