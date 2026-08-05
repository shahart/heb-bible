using HebBible.Api;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<HebBibleRepository>();

var app = builder.Build();

app.MapGet("/psukim/{name}", (string name, HebBibleRepository bible) =>
    Results.Json(bible.PsukimByName(name)));

app.Run();

public partial class Program;
