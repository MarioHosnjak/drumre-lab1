const apiUrl = 'http://localhost:8080/api/movies/filter'; // Replace with your API endpoint

let currentPage = 0;
let pageSize = 25;

let sortDirection = 'desc';
let sortBy = '';

let searchTitle = '';
let searchGenre = '';
let searchYear = '';

async function fetchMovies(page = 0, size = 5) {
    try {
        const response = await fetch(
            `${apiUrl}?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}&title=${searchTitle}&genre=${searchGenre}&year=${searchYear}`);
        const data = await response.json();
        console.log(data)
        populateTable(data.content);
        setupPagination(data.totalPages);
    } catch (error) {
        console.error('Error fetching movies:', error);
    }
}

function populateTable(movies) {
    const tableBody = document.getElementById('movie-table-body');
    tableBody.innerHTML = ''; // Clear previous rows
    let i = 1;
    movies.forEach(movie => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${currentPage * pageSize + i}.</td>
            <td>${movie.Title}</td>
            <td>${movie.Genre}</td>
            <td>${movie.Year}</td>
            <td>${movie.BoxOffice ? `$${movie.BoxOffice.toLocaleString()}` : 'N/A'}</td>
            <td>
                <button class="star-button" onclick="this.textContent = this.textContent === '★' ? '☆' : '★';">☆</button>
                <button class="delete" onclick="deleteMovie('${movie.id}')">Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
        i = i + 1;
    });
}

function setupPagination(totalPages) {
    const paginationDiv = document.getElementById('pagination');
    paginationDiv.innerHTML = '';
    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement('button');
        button.textContent = i + 1;
        if (i === currentPage) {
            button.classList.add('selected');
        }
        button.onclick = () => {
            currentPage = i;
            fetchMovies(currentPage, pageSize);
        };
        paginationDiv.appendChild(button);
    }
}

function changeSorting(columnClicked){
    sortBy = columnClicked;
    sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';

    // Clear all arrows
    document.querySelectorAll('th span').forEach(span => {
        span.textContent = '';
    });

    // Update arrow for the sorted column
    const arrowSpan = document.getElementById(`${columnClicked}-sort-arrow`);
    if (arrowSpan) {
        arrowSpan.textContent = sortDirection === 'asc' ? '▼' : '▲';
    }

    fetchMovies(currentPage, pageSize);
}

// Handle search input changes
function searchMoviesByTitle() {
    const searchBar = document.getElementById('search-bar-title');
    searchTitle = searchBar.value.trim();
    fetchMovies(0, pageSize); // Reset to the first page when searching
}

function searchMoviesByGenre() {
    const searchBar = document.getElementById('search-bar-genre');
    searchGenre = searchBar.value.trim();
    fetchMovies(0, pageSize);
    console.log("searchGenre");
}

function searchMoviesByYear() {
    const searchBar = document.getElementById('search-bar-year');
    searchYear = searchBar.value.trim();
    fetchMovies(0, pageSize); // Reset to the first page when searching
}

function updatePageSize(size) {
    pageSize = parseInt(size, 10); // Convert the value to an integer
    currentPage = 0; // Reset to the first page when the page size changes
    fetchMovies(currentPage, pageSize); // Fetch movies with the updated page size
}

// Delete movie function
async function deleteMovie(movieId) {
    if (confirm('Are you sure you want to delete this movie?')) {
        try {
            const response = await fetch(`http://localhost:8080/api/movies/delete/${movieId}`, {
                method: 'DELETE',
            });
            if (response.ok) {
                alert('Movie deleted successfully!');
                fetchMovies(currentPage, pageSize); // Refresh table
            } else {
                alert('Failed to delete movie.');
            }
        } catch (error) {
            console.error('Error deleting movie:', error);
        }
    }
}

// Initialize the table
fetchMovies(currentPage, pageSize);