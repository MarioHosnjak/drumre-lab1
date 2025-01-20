const apiUrl = 'http://localhost:8080/api/movies/filter';

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
        await populateTable(data.content);
        setupPagination(data.totalPages);
    } catch (error) {
        console.error('Error fetching movies:', error);
    }
}

async function populateTable(movies) {
    const likedMovies = await fetchLikedMovies(currentUserId);

    const tableBody = document.getElementById('movie-table-body');
    tableBody.innerHTML = '';
    let i = 1;
    movies.forEach(movie => {
        const isLiked = likedMovies.some(likedMovie => likedMovie.id === movie.id);
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${currentPage * pageSize + i}.</td>
            <td>${movie.Title}</td>
            <td>${movie.Genre}</td>
            <td>${movie.Year}</td>
            <td>${movie.BoxOffice ? `$${movie.BoxOffice.toLocaleString()}` : 'N/A'}</td>
            <td>
                <button class="star-button" style="font-size: 1em"
                        data-movie-id="${movie.id}"
                        onclick="toggleLike(this, '${movie.id}')">
                    ${isLiked ? '★' : '☆'}
                </button>
                <button style="font-size: 1em" class="delete" onclick="deleteMovie('${movie.id}')">Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
        i++;
    });
}

async function fetchLikedMovies(userId) {
    try {
        const response = await fetch(`/api/movies/liked-movies?userId=${userId}`);
        if (response.ok) {
            return await response.json();
        } else {
            console.error('Failed to fetch liked movies:', response.statusText);
            return [];
        }
    } catch (error) {
        console.error('Error fetching liked movies:', error);
        return [];
    }
}

async function toggleLike(button, movieId) {
    const isLiked = button.textContent === '★';
    const url = isLiked
        ? `/api/movies/unlike?userId=${encodeURIComponent(currentUserId)}&movieId=${encodeURIComponent(movieId)}`
        : `/api/movies/like?userId=${encodeURIComponent(currentUserId)}&movieId=${encodeURIComponent(movieId)}`;

    try {
        const response = await fetch(url, { method: 'POST' });
        if (response.ok) {
            button.textContent = isLiked ? '☆' : '★';
        } else {
            console.error('Failed to toggle like');
        }
    } catch (error) {
        console.error('Error toggling like:', error);
    }
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

    document.querySelectorAll('th span').forEach(span => {
        span.textContent = '';
    });

    const arrowSpan = document.getElementById(`${columnClicked}-sort-arrow`);
    if (arrowSpan) {
        arrowSpan.textContent = sortDirection === 'asc' ? '▼' : '▲';
    }

    fetchMovies(currentPage, pageSize);
}

function searchMoviesByTitle() {
    const searchBar = document.getElementById('search-bar-title');
    searchTitle = searchBar.value.trim();
    fetchMovies(0, pageSize);
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
    fetchMovies(0, pageSize);
}

function updatePageSize(size) {
    pageSize = parseInt(size, 10);
    currentPage = 0;
    fetchMovies(currentPage, pageSize);
}

async function deleteMovie(movieId) {
    if (confirm('Are you sure you want to delete this movie?')) {
        try {
            const response = await fetch(`http://localhost:8080/api/movies/delete/${movieId}`, {
                method: 'DELETE',
            });
            if (response.ok) {
                alert('Movie deleted successfully!');
                fetchMovies(currentPage, pageSize);
            } else {
                alert('Failed to delete movie.');
            }
        } catch (error) {
            console.error('Error deleting movie:', error);
        }
    }
}

fetchMovies(currentPage, pageSize);