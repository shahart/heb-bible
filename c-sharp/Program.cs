using HebBible.Api;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<HebBibleRepository>();

var app = builder.Build();

app.MapGet("/psukim", (HebBibleRepository bible) =>
    Results.Json(bible.TotalPsukim()));

app.MapGet("/psukim/{name}", (string name, HebBibleRepository bible, bool containsName = false) =>
    Results.Json(bible.PsukimByName(name, containsName)));

app.Run();

public partial class Program;
