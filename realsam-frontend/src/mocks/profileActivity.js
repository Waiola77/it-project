export const books = {
  "book-001": {
    id: "book-001",
    title: "Lanterns at Low Tide",
    author: "Mara Ellison",
    coverColour: "#246176",
  },
  "book-002": {
    id: "book-002",
    title: "The Sunday Orchard",
    author: "Amina Vale",
    coverColour: "#3f7657",
  },
  "book-003": {
    id: "book-003",
    title: "The Small Museum of Lost Things",
    author: "Iris Cole",
    coverColour: "#8a503d",
  },
  "book-004": {
    id: "book-004",
    title: "A Map of Distant Stars",
    author: "Elias North",
    coverColour: "#414b85",
  },
  "book-005": {
    id: "book-005",
    title: "The Clockmaker's Secret",
    author: "Clara Finch",
    coverColour: "#76537d",
  },
  "book-006": {
    id: "book-006",
    title: "Brave New Horizons",
    author: "Noah Reed",
    coverColour: "#376b70",
  },
};

export const profileActivityByUser = {
  "user-001": {
    liked: ["book-002", "book-003", "book-005"],
    rejected: ["book-004"],
    read: [
      { bookId: "book-001", progress: 100 },
      { bookId: "book-002", progress: 68 },
      { bookId: "book-003", progress: 31 },
    ],
  },
  "user-002": {
    liked: ["book-004", "book-006"],
    rejected: ["book-003"],
    read: [
      { bookId: "book-004", progress: 100 },
      { bookId: "book-006", progress: 74 },
    ],
  },
  "user-003": {
    liked: ["book-001", "book-005"],
    rejected: ["book-002"],
    read: [
      { bookId: "book-005", progress: 100 },
      { bookId: "book-001", progress: 46 },
    ],
  },
  "user-004": {
    liked: ["book-006"],
    rejected: [],
    read: [{ bookId: "book-006", progress: 83 }],
  },
  "user-005": {
    liked: ["book-003", "book-004"],
    rejected: ["book-006"],
    read: [
      { bookId: "book-003", progress: 100 },
      { bookId: "book-004", progress: 46 },
    ],
  },
};
